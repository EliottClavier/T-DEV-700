package com.api.auth.controller;

import com.api.auth.model.ShopLoginCredentials;
import com.api.auth.security.JWTUtil;
import com.api.auth.security.providers.ShopAuthenticationProvider;
import com.api.bank.model.entity.Shop;
import com.api.bank.service.AccountService;
import com.api.bank.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/auth/shop")
public class AuthShopController {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private ShopAuthenticationProvider shopAuthenticationProvider;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ShopService shopService;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity registerHandler(@RequestBody Shop shop){
        Shop shopRegisterResponse = shopService.registerShop(shop);
        if (shopRegisterResponse != null) {
            accountService.createShopAccount(shop);
            return new ResponseEntity<>(Collections.singletonMap("message", "Shop registered successfully. It needs to be whitelisted."), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.singletonMap("message", "Shop already registered."), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity loginHandler(@RequestBody ShopLoginCredentials body){
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getName(), body.getPassword());
            shopAuthenticationProvider.authenticate(authInputToken);
            String token = jwtUtil.generateToken(body.getName(), "Shop Connection");
            return new ResponseEntity<>(Collections.singletonMap("token", token), HttpStatus.OK);
        } catch (AuthenticationException authExc) {
            if (authExc.getMessage().equals("Not whitelisted.")) {
                return new ResponseEntity<>(Collections.singletonMap("message", authExc.getMessage()), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(Collections.singletonMap("message", authExc.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
}
