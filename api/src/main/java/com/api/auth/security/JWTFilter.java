package com.api.auth.security;

import com.api.auth.security.details.service.ManagerDetailsService;
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
    private JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")){
            String jwt = authHeader.substring(7);
            if(jwt.isBlank()){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
            } else {
                try{
                    String subject = jwtUtil.getSubjectFromToken(jwt);
                    UsernamePasswordAuthenticationToken authToken;
                    if (Objects.equals(subject, "Manager Connection")) {
                        String username = jwtUtil.validateTokenAndRetrieveSubject(jwt, subject);
                        UserDetails managerDetails = managerDetailsService.loadUserByUsername(username);
                        authToken = new UsernamePasswordAuthenticationToken(username, managerDetails.getPassword(), managerDetails.getAuthorities());
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }

                    } else if (Objects.equals(subject, "TPE Connection")) {
                        String mac = jwtUtil.validateTokenAndRetrieveSubject(jwt, subject);
                        UserDetails tpeDetails = tpeDetailsService.loadUserByUsername(mac);
                        authToken = new UsernamePasswordAuthenticationToken(mac, tpeDetails.getPassword(), tpeDetails.getAuthorities());
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                } catch(JWTVerificationException exc){
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token");
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}