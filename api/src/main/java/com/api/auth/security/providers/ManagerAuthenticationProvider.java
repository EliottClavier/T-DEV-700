package com.api.auth.security.providers;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

public class ManagerAuthenticationProvider extends DaoAuthenticationProvider {
    public ManagerAuthenticationProvider() {
        super();
    }
}
