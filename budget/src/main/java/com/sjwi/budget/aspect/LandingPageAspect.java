package com.sjwi.budget.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sjwi.budget.model.user.BudgetUser;
import com.sjwi.budget.service.UserService;

@Aspect
@Configuration
public class LandingPageAspect {
	
	@Autowired
	UserService userService;
	@Before("(execution(* com.sjwi.budget.controller.*.*(..)))")
	public void initializeUser() {
		BudgetUser user = (BudgetUser) userService.loadUserByUsername("demo_user");
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
	}
}