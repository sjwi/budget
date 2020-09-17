package com.sjwi.budget.mail;

public class MailConstants {

	public static String DEFAULT_FROM_ADDRESS;
	public static String ADMIN_DISTRIBUTION_LIST;
	public static final String PDF_SUBJECT = "View Your Monthly Cash Budget";
	public static final String PDF_BODY = "See attached pdf to review your monthly cash budget";
	
	public static void initializeMailConstants(String defaultFromAddress, String adminDistributionList) {
		DEFAULT_FROM_ADDRESS = defaultFromAddress;
		ADMIN_DISTRIBUTION_LIST = adminDistributionList;
	}
}
