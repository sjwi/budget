package com.sjwi.budget.model;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;


public class PdfGenerator {

	private static final String SUFFIX = ".pdf";
	private static final String PREFIX = "pdfdoc";
	public static final String PDF_SUB_DIRECTORY = "pdf_dir";
	private static final int DEF_FONT = 12;
	private static final int DEF_TITLE_FONT = 16;
	private static final int DEF_LEADING = 18;
	private final Budget budget;
	private final String file;
	private final String contextFilePath;
	private PdfWriter writer;
	private Document document;

	public PdfGenerator(Budget budget) throws Exception {
		String root = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext().getRealPath("/");
		contextFilePath = PDF_SUB_DIRECTORY + "/" + PREFIX + "_" + System.currentTimeMillis() + SUFFIX;
		file = root + contextFilePath;
		new File(root + PDF_SUB_DIRECTORY).mkdir();
		document = new Document();
		writer = PdfWriter.getInstance(document, new FileOutputStream(file));
		this.budget = budget;
		
	}
	
	public String buildFile(String account) throws Exception {

		try {
			writer.setPageEvent(new DrawHeaderAndFooter(account));
			document.open();
			document.add(buildTitle(budget.getName()));
			document.add(buildBudgetTable());
			document.add(buildTitle("Bill Calculator"));
			document.add(buildBillCalculator());
		} catch (Exception e) {
			throw new Exception("Unable to build pdf file for budget " + budget.getName(), e);
		} finally {
			cleanup();
		}
		
		return file;
	}

	private Element buildBillCalculator() {
		PdfPTable table = new PdfPTable(6);
		budget.getDenominationMapForItems().forEach((k,v) -> {
			table.addCell(buildCellFromText("$"+k+" x " + v,Element.ALIGN_CENTER));
		});
		return table;
	}

	private PdfPTable buildBudgetTable() {
		PdfPTable table = new PdfPTable(2);
		table.addCell(buildTitleFromText("Budget Item",Element.ALIGN_LEFT));
		table.addCell(buildTitleFromText("Amount",Element.ALIGN_RIGHT));
		for (Item item: budget.getItems()) {
			table.addCell(buildCellFromText(item.getName(),Paragraph.ALIGN_LEFT));
			table.addCell(buildCellFromText("$" + item.getAmount(), Paragraph.ALIGN_RIGHT));
		}
		table.addCell("");
		table.addCell(buildCellFromText("Total: $" + budget.getItems().stream().map(i -> i.getAmount()).reduce(Double::sum).get(), Paragraph.ALIGN_RIGHT));
		table.setSpacingAfter(20);
		return table;
	}
	
	private PdfPCell buildCellFromText(String text, int alignment) {
		return buildCellFromTextWithFont(text,alignment,FontFactory.getFont(FontFactory.HELVETICA, DEF_FONT, BaseColor.BLACK));
	}

	private PdfPCell buildTitleFromText(String text, int alignment) {
		return buildCellFromTextWithFont(text,alignment,FontFactory.getFont(FontFactory.HELVETICA_BOLD, DEF_FONT + 1, BaseColor.BLACK));
	}
	
	private PdfPCell buildCellFromTextWithFont(String text, int alignment, Font font) {
		PdfPCell cell = new PdfPCell();
		cell.setHorizontalAlignment(alignment);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		Paragraph paragraph = new Paragraph(text,font);
		paragraph.setLeading(0,1);
		paragraph.setAlignment(alignment);
		cell.setMinimumHeight(20);
		cell.addElement(paragraph);
		return cell;
	}

	private Paragraph buildTitle(String title) {
		Paragraph titleParagraph = new Paragraph(new Chunk(title,FontFactory.getFont(FontFactory.HELVETICA_BOLD, DEF_TITLE_FONT, BaseColor.BLACK)));
		titleParagraph.setAlignment(Element.ALIGN_CENTER);
		titleParagraph.setLeading(DEF_LEADING);
		titleParagraph.setSpacingAfter(25);
		return titleParagraph;
	}


	private void cleanup() {
		document.close();
	}
	
	public String getFileName() {
		return file;
	}

	public void removeFile() throws Exception {
		Files.deleteIfExists(Paths.get(file));
	}	

	public String getContextFilePath() {
		return contextFilePath;
	}
	
	public class DrawHeaderAndFooter extends PdfPageEventHelper {
		private final String headerText;
		private final String account;
		
		DrawHeaderAndFooter(String account){
			this.headerText = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
			this.account = account;
		}
		@Override
		public void onStartPage(PdfWriter writer, Document document) {
			addHeader(headerText, 545, 815, Element.ALIGN_RIGHT);
			addHeader("Account #" + account, 55, 815, Element.ALIGN_LEFT);
			addFooter(writer, document.getPageNumber());
		}
		private void addHeader(String headerText, int x, int y, int alignment) {
			ColumnText.showTextAligned(writer.getDirectContent(), 
					alignment, 
					new Phrase(headerText, FontFactory.getFont(FontFactory.HELVETICA,10,BaseColor.BLACK)), x, y, 0);
		}
		private void addFooter(PdfWriter writer, int count) {
			Font footerFont = FontFactory.getFont(FontFactory.HELVETICA,10,BaseColor.BLACK);
			Phrase pageNumber = new Phrase(Integer.toString(count),footerFont);
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, pageNumber, 545, 20, 0);
		}
	}

}
