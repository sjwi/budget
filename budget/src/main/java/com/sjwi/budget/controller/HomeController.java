package com.sjwi.budget.controller;

import static com.sjwi.budget.mail.MailConstants.PDF_BODY;
import static com.sjwi.budget.mail.MailConstants.PDF_SUBJECT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sjwi.budget.mail.Mailer;
import com.sjwi.budget.model.Budget;
import com.sjwi.budget.model.Item;
import com.sjwi.budget.model.PdfGenerator;
import com.sjwi.budget.model.mail.EmailWithAttachment;
import com.sjwi.budget.model.user.BudgetUser;
import com.sjwi.budget.service.BudgetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	
	@Autowired
	Mailer mailer;
	
	@Autowired
	BudgetService budgetService;
	
	@Autowired
	ServletContext servletContext;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@RequestMapping("/")
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView("home");
		mv.addObject("templates", budgetService.getAllTemplates());
		mv.addObject("budgets", budgetService.getAllBudgets());
		return mv;
	}

	@RequestMapping(value="/login", method=RequestMethod.GET)
	public ModelAndView login() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (null != auth && !("anonymousUser").equals(auth.getName())) {
			return new ModelAndView("redirect:/");
		}
		return new ModelAndView("login");
	}
	
	@RequestMapping(value = "/create/template", method = RequestMethod.POST)
	public void createTemplate(HttpServletResponse response, HttpServletRequest request, 
			@RequestParam("item_name") List<String> items, @RequestParam("item_max_denom") List<Integer> maxDenomination,
			@RequestParam("item_amount") List<Double> amounts, @RequestParam("budgetName") String templateName) throws IOException {
		Budget templateBudget = new Budget(0,templateName,"", 
				IntStream.range(0, items.size()).boxed()
						.map(i -> new Item(0,items.get(i),amounts.get(i),maxDenomination.get(i)))
						.filter(i -> i.getName() != null && !i.getName().isEmpty() && i.getAmount() != null)
						.collect(Collectors.toList())
				);
		budgetService.createTemplate(templateBudget);
		response.sendRedirect(request.getContextPath() + "/#create-budget");
	}
	
	@RequestMapping(value = "/create/budget", method = RequestMethod.POST)
	public void createBudget(HttpServletResponse response, HttpServletRequest request, 
			@RequestParam("template") Optional<Integer> template, 
			@RequestParam(name="budgetDescription", required=false) String description, 
			@RequestParam(value="month", required=true) int month) throws IOException {
		budgetService.createBudget(template,description,month);
		response.sendRedirect(request.getContextPath() + "/");
	}

	@RequestMapping(value = "/budget/edit", method = {RequestMethod.GET})
	public void editTemplate(HttpServletResponse response, 
			HttpServletRequest request,
			@RequestParam(value="item_name[]") List<String> items, 
			@RequestParam(value="budgetId", required=true) int budgetId, 
			@RequestParam("item_amount[]") List<Double> amounts, 
			@RequestParam("item_max_denom[]") List<Integer> denominations, 
			@RequestParam(name="redirect", required=false) String redirect, 
			@RequestParam(name="budgetDescription", required=false) String description, 
			@RequestParam("budgetName") String budgetName) throws IOException {
		Budget templateBudget = new Budget(budgetId,budgetName,description,
				IntStream.range(0, items.size()).boxed()
						.map(i -> new Item(0,items.get(i),amounts.get(i),denominations.get(i)))
						.filter(i -> i.getName() != null && !i.getName().isEmpty() && i.getAmount() != null)
						.collect(Collectors.toList())
				);
		budgetService.editBudget(templateBudget);
		if (redirect != null && !redirect.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/");
		}
	}
	
	@RequestMapping(value = "/budget/edit", method = {RequestMethod.POST})
	public void editTemplateViaForm(HttpServletResponse response, 
			HttpServletRequest request,
			@RequestParam(value="item_name") List<String> items, 
			@RequestParam(value="budgetId", required=true) int budgetId, 
			@RequestParam("item_amount") List<Double> amounts, 
			@RequestParam("item_max_denom") List<Integer> denominations, 
			@RequestParam(name="redirect", required=false) String redirect, 
			@RequestParam(name="budgetDescription", required=false) String description, 
			@RequestParam("budgetName") String budgetName) throws IOException {
		Budget templateBudget = new Budget(budgetId,budgetName,description,
				IntStream.range(0, items.size()).boxed()
						.map(i -> new Item(0,items.get(i),amounts.get(i),denominations.get(i)))
						.filter(i -> i.getName() != null && !i.getName().isEmpty() && i.getAmount() != null)
						.collect(Collectors.toList())
				);
		budgetService.editBudget(templateBudget);
		if (redirect != null && !redirect.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/");
		}
	}

	@RequestMapping(value = "/budget/disable/{id}", method = RequestMethod.POST)
	public void deleteBudget(HttpServletResponse response, @PathVariable int id) throws IOException {
		budgetService.disableBudget(id);
	}

	@RequestMapping(value = "/get-budget/{id}", method = RequestMethod.GET)
	public ModelAndView getTemplate(HttpServletResponse response, HttpServletRequest request,
			@PathVariable int id, 
			@RequestParam(required = true, name="view") String view) throws IOException {
		ModelAndView mv = new ModelAndView(view);
		mv.addObject("budget",budgetService.getBudget(id));
		return mv;
	}
	@RequestMapping(value = {"download/{id}/{pdf_name}"}, method = RequestMethod.GET)
	public void downloadPdf(HttpServletRequest request, HttpServletResponse response, Authentication auth,
			@PathVariable int id) throws IOException {
		try {
			PdfGenerator pdfGenerator = new PdfGenerator(budgetService.getBudget(id));
			response.setContentType("application/pdf; name=\"budget.pdf\"");
            response.addHeader("Content-Disposition", "inline; filename=\"budget.pdf\"");
            Files.copy(Paths.get(pdfGenerator.buildFile(((BudgetUser)auth.getPrincipal()).getAccount())), response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/error");
		}
	}
	@RequestMapping(value = {"email/budget/{id}"}, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void emailBudget(HttpServletRequest request, HttpServletResponse response, Authentication auth,
			@PathVariable int id, @RequestParam String emailTo) throws Exception {
		PdfGenerator pdfGenerator = new PdfGenerator(budgetService.getBudget(id));
		pdfGenerator.buildFile(((BudgetUser)auth.getPrincipal()).getAccount());
		Map<String, String> attachments = new HashMap<>();
		attachments.put(pdfGenerator.getFileName(), "budget.pdf");
		mailer.sendMail(new EmailWithAttachment().setAttachments(attachments).setTo(emailTo.replace(";", ",")).setSubject(PDF_SUBJECT).setBody(PDF_BODY));
	}
}
