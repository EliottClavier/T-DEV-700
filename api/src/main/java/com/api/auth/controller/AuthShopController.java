package com.api.auth.controller;

import com.api.auth.model.ShopLoginCredentials;
import com.api.auth.model.TpeLoginCredentials;
import com.api.auth.security.JWTUtil;
import com.api.auth.security.providers.ShopAuthenticationProvider;
import com.api.bank.model.entity.Shop;
import com.api.bank.model.entity.Tpe;
import com.api.bank.repository.ShopRepository;
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
@RequestMapping("/auth/shop")
public class AuthShopController {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private ShopAuthenticationProvider shopAuthenticationProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ShopRepository shopRepository;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public Map<String, Object> registerHandler(@RequestBody Shop shop){
        if (!shopRepository.existsByName(shop.getName())) {
            String encodedPass = passwordEncoder.encode(shop.getPassword());
            shop.setPassword(encodedPass);
            shop.setWhitelisted(false);
            shopRepository.save(shop);
            return Collections.singletonMap("message", "Shop registered successfully. It needs to be whitelisted.");
        } else {
            return Collections.singletonMap("message", "Shop already registered.");
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public Map<String, Object> loginHandler(@RequestBody ShopLoginCredentials body){
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getName(), body.getPassword());
            shopAuthenticationProvider.authenticate(authInputToken);
            String token = jwtUtil.generateToken(body.getName(), "Shop Connection");
            return Collections.singletonMap("token", token);
        } catch (AuthenticationException authExc) {
            return Collections.singletonMap("message", authExc.getMessage());
        }
    }
}
