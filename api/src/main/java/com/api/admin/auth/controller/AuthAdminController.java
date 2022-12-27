package com.api.admin.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/auth")
public class AuthAdminController {

    @GetMapping( value = {"/login/", "/login"})
    public String getTemplate() {
        return "loginView";
    }

    @GetMapping( value = {"/login/test/", "/login/test"})
    public String getAdmin() {
        System.out.println("Get Connected ! ");
        return null;
    }
}