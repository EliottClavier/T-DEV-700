package com.api.admin.auth.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/auth/login")
public class AuthAdminController {

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public String getPage(Model model) {
        return "index";
    }
}