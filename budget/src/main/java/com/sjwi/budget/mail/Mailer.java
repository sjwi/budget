package com.sjwi.budget.mail;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.sjwi.budget.model.mail.Email;

@Component
public class Mailer {
	
	@Value("${BUDGET_PREVIEW_MAIL_LEVEL}")
	private String mailLevel;
	
	@Autowired
	Environment env;
	
	@Autowired
	Session session;
	
	public void sendMail(Email email) throws MessagingException {
		sendMessage(email.getEmailMessage(session));
	}

	private void sendMessage(Message message) throws MessagingException {
		if ("LIVE".equals(mailLevel)) {
			Transport.send(message);
		}
	}
}
