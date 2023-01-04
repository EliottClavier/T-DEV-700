package com.api.auth.security;

import com.api.auth.security.details.service.ManagerDetailsService;
import com.api.auth.security.details.service.ShopDetailsService;
import com.api.auth.security.details.service.TpeDetailsService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private ManagerDetailsService managerDetailsService;

    @Autowired
    private TpeDetailsService tpeDetailsService;

    @Autowired
    private ShopDetailsService shopDetailsService;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * A supplementary filter added to SecurityFilterChain to check if JWT Token passed in the request is valid
     * on either TPE, Shop or Manager perspective.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        // Check if Authorization header is present and well formed
        if(authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")){
            String jwt = authHeader.substring(7);
            if(jwt.isBlank()){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
            } else {
                try{
                    String subject = jwtUtil.getSubjectFromToken(jwt);
                    UsernamePasswordAuthenticationToken authToken;
                    // Manager perspective
                    if (Objects.equals(subject, "Manager Connection")) {
                        String username = jwtUtil.validateTokenAndRetrieveSubject(jwt, subject);
                        UserDetails managerDetails = managerDetailsService.loadUserByUsername(username);
                        authToken = new UsernamePasswordAuthenticationToken(username, managerDetails.getPassword(), managerDetails.getAuthorities());
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }

                    // TPE perspective
                    } else if (Objects.equals(subject, "TPE Connection")) {
                        String androidId = jwtUtil.validateTokenAndRetrieveSubject(jwt, subject);
                        UserDetails tpeDetails = tpeDetailsService.loadUserByUsername(androidId);
                        authToken = new UsernamePasswordAuthenticationToken(androidId, tpeDetails.getPassword(), tpeDetails.getAuthorities());
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }

                    // Shop perspective
                    } else if (Objects.equals(subject, "Shop Connection")) {
                        String name = jwtUtil.validateTokenAndRetrieveSubject(jwt, subject);
                        UserDetails shopDetails = shopDetailsService.loadUserByUsername(name);
                        authToken = new UsernamePasswordAuthenticationToken(name, shopDetails.getPassword(), shopDetails.getAuthorities());
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }

                } catch(JWTVerificationException exc){
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token");
                }
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}