package com.api;


import com.api.bank.model.entity.Base;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@EnableJpaRepositories("com.api.bank.repository")
@SpringBootApplication(scanBasePackages = {"com.api", "com.api.apk", "com.api.tpe", "com.api.bank"})
public class ApiApplication {


    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
