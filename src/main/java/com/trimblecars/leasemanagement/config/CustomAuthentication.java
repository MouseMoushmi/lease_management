package com.trimblecars.leasemanagement.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomAuthentication implements Authentication {

    private final UserDetails userDetails;
    private final Object credentials;
    private final Object details;
    private boolean authenticated = true;

    public CustomAuthentication(UserDetails userDetails) {
        this(userDetails, null, null);
    }

    public CustomAuthentication(UserDetails userDetails, Object credentials) {
        this(userDetails, credentials, null);
    }

    public CustomAuthentication(UserDetails userDetails, Object credentials, Object details) {
        this.userDetails = userDetails;
        this.credentials = credentials;
        this.details = details;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetails.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return credentials; // Return token or password if available
    }

    @Override
    public Object getDetails() {
        return details; // Return additional details if available
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        this.authenticated = authenticated;
    }

    @Override
    public String getName() {
        return userDetails.getUsername();
    }
}