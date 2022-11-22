package com.api.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ApiApplication {
    @RequestMapping("/")
    public String home() {
        return "<h1>API is running !<h1>";
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
