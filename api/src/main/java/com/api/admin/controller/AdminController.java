package com.api.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping( path = "/login", method = RequestMethod.GET)
    public String getLoginTemplate() {
        return "login";
    }

    @RequestMapping( path = "/dashboard", method = RequestMethod.GET)
    public String getDashboardTemplate() {
        return "dashboard";
    }

    @RequestMapping( path = "/qr-code/generate", method = RequestMethod.GET)
    public String getQrCodeGeneratorTemplate() {
        return "qrCodeGenerator";
    }

    @RequestMapping( path = "/qr-code/{qrCodeUuid}", method = RequestMethod.GET)
    public String getQrCodeViewerTemplate(@PathVariable String qrCodeUuid) {
        return "qrCodeViewer";
    }

}
