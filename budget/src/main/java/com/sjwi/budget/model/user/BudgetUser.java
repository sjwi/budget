package com.sjwi.budget.model.user;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class BudgetUser extends User {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8215868281708962565L;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final String fullName;
	
	public BudgetUser(String username, String firstName, String lastName, String email, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username,password,true,true,true,true,authorities);
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.fullName = firstName + " " + lastName;
	}

	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getEmail() {
		return email;
	}
	public String getFullName() {
		return fullName;
	}
}
