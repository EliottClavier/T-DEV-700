package com.api.auth.evaluator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Evaluates the permission of a user to access a TPE register route.
 * It depends if the http request contains a valid fixed token in the correct http header.
 */
@Component
public class FixedTokenPermissionEvaluator implements PermissionEvaluator {

    @Value("${tpe.register.secret.header}")
    private String secretHeader;

    @Value("${tpe.register.secret.key}")
    private String secretKey;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject instanceof SecurityContextHolderAwareRequestWrapper requestWrapper) {
            String fixedToken = requestWrapper.getHeader(secretHeader);
            return fixedToken != null && fixedToken.equals(secretKey);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
