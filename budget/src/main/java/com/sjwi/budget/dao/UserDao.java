package com.sjwi.budget.dao;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.sjwi.budget.model.user.BudgetUser;

public interface UserDao {

	void saveUser(BudgetUser user);

	void saveUserAuthorities(String username, Collection<GrantedAuthority> authorities);

	User getUser(String username);
}
