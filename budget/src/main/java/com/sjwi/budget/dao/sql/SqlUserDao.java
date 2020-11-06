package com.sjwi.budget.dao.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import com.sjwi.budget.dao.UserDao;
import com.sjwi.budget.model.user.BudgetUser;

@Repository
public class SqlUserDao implements UserDao {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private Map<String,String> queryStore;

	@Override
	public void saveUser(BudgetUser user) {
		deleteUser(user.getUsername());
		jdbcTemplate.update(queryStore.get("saveUser"), new Object[] {user.getUsername(),user.getPassword(),user.getFirstName(),user.getLastName(),user.getEmail()});
	}
	
	@Override
	public void saveUserAuthorities(String username, Collection<GrantedAuthority> authorities) {
		authorities.stream().forEach(a -> {
			jdbcTemplate.update(queryStore.get("saveUserAuthorities"), new Object[] {username, a.getAuthority()});
		});
	}

	@Override
	public User getUser(String username) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("username", username);
		return namedParameterJdbcTemplate.query(queryStore.get("getUserByUsername"),parameters,r -> {
			if (r.next()){
				return new BudgetUser(r.getString("USERNAME"),
						r.getString("FIRSTNAME"),
						r.getString("LASTNAME"),
						r.getString("EMAIL"),
						r.getString("PASSWORD"),
						r.getString("ACCOUNT"),
						getUserAuthorities(r.getString("USERNAME")));
			} else {
				return null;
			}
		});
	}
	
	private Collection<? extends GrantedAuthority> getUserAuthorities(String username) {
		return jdbcTemplate.query(queryStore.get("getAuthoritiesByUsername"), new Object[] {username}, r -> {
			List <SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
			while (r.next()) {
				authorities.add(new SimpleGrantedAuthority(r.getString("authority")));
			}
			return authorities;
		});
	}

	private void deleteUser(String username) {
		jdbcTemplate.update(queryStore.get("deleteUser"), new Object[] {username});
	}
}
