package com.api.auth.controller;

import com.api.auth.annotation.TpeRegisterSecuredRoute;
import com.api.auth.model.TpeLoginCredentials;
import com.api.auth.model.TpeRegisterCredentials;
import com.api.auth.security.JWTUtil;
import com.api.auth.security.providers.TpeAuthenticationProvider;
import com.api.auth.service.AuthService;
import com.api.bank.model.entity.Tpe;
import com.api.bank.repository.TpeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth/tpe")
public class AuthTpeController {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private TpeAuthenticationProvider tpeAuthenticationProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TpeRepository tpeRepository;

    @Autowired
    private AuthService authService;

    @TpeRegisterSecuredRoute
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity registerHandler(
            @RequestBody TpeRegisterCredentials tpeRegisterCredentials,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (!tpeRepository.existsByAndroidId(tpeRegisterCredentials.getAndroidId())) {
            String randomPassword = authService.alphaNumericString(12);
            String encodedPass = passwordEncoder.encode(randomPassword);
            Tpe tpe = new Tpe(tpeRegisterCredentials.getAndroidId(), encodedPass);
            tpe.setWhitelisted(false);
            tpeRepository.save(tpe);
            // Return list with two attributes: message and password
            return new ResponseEntity<>(
                Map.of(
                "message", "TPE registered successfully. It needs to be whitelisted.",
                "password", randomPassword
                ), HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(Collections.singletonMap("message", "TPE already registered."), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity loginHandler(@RequestBody TpeLoginCredentials body){
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getAndroidId(), body.getPassword());
            tpeAuthenticationProvider.authenticate(authInputToken);
            String token = jwtUtil.generateToken(body.getAndroidId(), "TPE Connection");
            return new ResponseEntity<>(Collections.singletonMap("token", token), HttpStatus.OK);
        } catch (AuthenticationException authExc) {
            if (authExc.getMessage().equals("Not whitelisted.")) {
                return new ResponseEntity<>(Collections.singletonMap("message", authExc.getMessage()), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(Collections.singletonMap("message", authExc.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
}
