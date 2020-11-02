package com.sjwi.budget.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
	  throws Exception {
	    auth.userDetailsService(userDetailsService);
	}
	
	@Override
    protected void configure(HttpSecurity httpSecurity)
      throws Exception {
        httpSecurity.authorizeRequests()
        	.antMatchers("/**").permitAll()
        	.and()
        	.formLogin()
        	.defaultSuccessUrl("/")
          .and().requestCache().requestCache(requestCache())
        	.and().logout()
        	.logoutSuccessUrl("/login")
        	.and().headers()
			.frameOptions().sameOrigin()
			.httpStrictTransportSecurity().disable();;
        httpSecurity.csrf().disable(); //Required for AJAX requests to be authorized
    }
	
	@Bean
	public RequestCache requestCache() {
	   return new HttpSessionRequestCache();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}
