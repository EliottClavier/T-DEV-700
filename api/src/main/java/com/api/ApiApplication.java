package com.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(value = {"com.api.bank.repository", "com.api.auth.repository"})
@SpringBootApplication(scanBasePackages = {"com.api", "com.api.apk", "com.api.transaction", "com.api.transaction.shop", "com.api.bank", "com.api.transaction.tpe", "com.api.auth"})
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
