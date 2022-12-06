package com.api.auth.controller;

import com.api.auth.model.ManagerLoginCredentials;
import com.api.auth.security.JWTUtil;
import com.api.auth.security.providers.ManagerAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth/manager")
public class AuthManagerController {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private ManagerAuthenticationProvider managerAuthenticationProvider;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public Map<String, Object> loginHandler(@RequestBody ManagerLoginCredentials body){
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword());
            managerAuthenticationProvider.authenticate(authInputToken);
            String token = jwtUtil.generateToken(body.getUsername(), "Manager Connection");
            return Collections.singletonMap("token", token);
        } catch (AuthenticationException authExc) {
            return Collections.singletonMap("message", "Wrong credentials.");
        }
    }
}
