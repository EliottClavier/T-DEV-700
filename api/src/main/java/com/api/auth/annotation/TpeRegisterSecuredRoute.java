package com.api.auth.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to secure a route with a fixed token.
 * The token is defined in the application.properties file.
 * It is invoked in /auth/tpe/register route.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission(#request, 'FIXED_TOKEN')")
public @interface TpeRegisterSecuredRoute {
}
