package com.sjwi.budget.model.user;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class BudgetUserDetails implements UserDetails{

    private static final long serialVersionUID = 1L;
    private BudgetUser user;

    Collection<GrantedAuthority> authorities=null;
    
    public BudgetUserDetails(BudgetUser user) {
    	this.user = user;
    	this.authorities = user.getAuthorities();
    }

    public User getUser() {
        return user;
    }

    public void setUser(BudgetUser user) {
        this.user = user;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities)
    {
        this.authorities=authorities;
    }

    public String getPassword() {
        return user.getPassword();
    }

    public String getUsername() {
        return user.getUsername();
    }
    
    public String getFirstname() {
    	return user.getFirstName();
    }

    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    public boolean isEnabled() {
        return user.isEnabled();
    }

}