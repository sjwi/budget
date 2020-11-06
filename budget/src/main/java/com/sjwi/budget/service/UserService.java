package com.sjwi.budget.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sjwi.budget.dao.UserDao;
import com.sjwi.budget.model.user.BudgetUser;

@Service("userDetailsService")
public class UserService implements UserDetailsService {
	
	@Autowired
	UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public BudgetUser createUser(String username, String firstName, String lastName, String email, String password, String account, List<String> authorityNames) {
		password = passwordEncoder.encode(password);
	    List <SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
	    authorityNames.stream().forEach(a -> {
			authorities.add(new SimpleGrantedAuthority(a.toUpperCase()));
	    });
	    BudgetUser user = new BudgetUser(username.trim().toLowerCase(), firstName, lastName, email, account, password, authorities);
	    userDao.saveUser(user);
		userDao.saveUserAuthorities(user.getUsername(),user.getAuthorities());
	    return user;
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
	    return userDao.getUser(username.trim().toLowerCase());
	}
}