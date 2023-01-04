package com.api.auth.controller;

import com.api.auth.model.ManagerLoginCredentials;
import com.api.auth.security.JWTUtil;
import com.api.auth.security.providers.ManagerAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * This method is used to authenticate the manager
     *
     * @param body contains the username and password of the manager
     * @return ResponseEntity with the token if the authentication is successful, otherwise it returns a 401 status code
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity loginHandler(@RequestBody ManagerLoginCredentials body){
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword());
            managerAuthenticationProvider.authenticate(authInputToken);
            String token = jwtUtil.generateToken(body.getUsername(), "Manager Connection");
            return new ResponseEntity<>(Collections.singletonMap("token", token), HttpStatus.OK);
        } catch (AuthenticationException authExc) {
            return new ResponseEntity<>(Collections.singletonMap("message", authExc.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
}
