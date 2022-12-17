package com.api;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableJpaRepositories(value = {"com.api.bank.repository", "com.api.auth.repository", "com.api.transaction.tpe.repository"})
@SpringBootApplication(scanBasePackages = {"com.api", "com.api.apk","com.api.transaction",  "com.api.bank", "com.api.auth"})
@EnableTransactionManagement()
public class ApiApplication {


    public static void main(String[] args) {


        SpringApplication.run(ApiApplication.class, args);
    }

}
