package com.api;


import com.api.bank.model.entity.Account;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//@EntityScan("com.api.bank.model")
//@ComponentScan(basePackages= {"com.api.bank.repository"})
@EnableJpaRepositories("com.api.bank.repository")
@SpringBootApplication(scanBasePackages = {"com.api.apk", "com.api.tpe", "com.api.bank"})
public class ApiApplication {



    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);



    }

}
