package com.api.admin.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthAdminController {

    @GetMapping(value = "/admin/auth/login/")
    public String getTemplate(@RequestParam(name="name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "thymeleafTemplate";
    }

    @GetMapping(value = "/admin/whitelist/")
    public String whitelist() {
        return "thymeleafTemplate";
    }
}