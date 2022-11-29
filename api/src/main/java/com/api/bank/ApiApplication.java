package com.api.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;

@RestController
@SpringBootApplication
@EntityScan("com.api.bank.model")
@ComponentScan(basePackages= {"com.api.bank.repository"})
@EnableJpaRepositories("com.api.bank.repository")
public class ApiApplication {

    @RequestMapping("/")
    public String home() {
        return "<h1>API is running !<h1>";
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
