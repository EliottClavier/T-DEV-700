package com.api.auth.controller;

import com.api.auth.model.TpeLoginCredentials;
import com.api.auth.security.JWTUtil;
import com.api.auth.security.providers.TpeAuthenticationProvider;
import com.api.bank.model.entity.Tpe;
import com.api.bank.repository.TpeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public Map<String, Object> registerHandler(@RequestBody Tpe tpe){
        if (!tpeRepository.existsByMac(tpe.getMac())) {
            String encodedPass = passwordEncoder.encode(tpe.getSerial());
            tpe.setSerial(encodedPass);
            tpe.setWhitelisted(false);
            tpeRepository.save(tpe);
            return Collections.singletonMap("message", "TPE registered successfully. It needs to be whitelisted.");
        } else {
            return Collections.singletonMap("message", "TPE already registered.");
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public Map<String, Object> loginHandler(@RequestBody TpeLoginCredentials body){
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getMac(), body.getSerial());
            tpeAuthenticationProvider.authenticate(authInputToken);
            String token = jwtUtil.generateToken(body.getMac(), "TPE Connection");
            return Collections.singletonMap("token", token);
        } catch (AuthenticationException authExc) {
            return Collections.singletonMap("message", authExc.getMessage());
        }
    }
}
