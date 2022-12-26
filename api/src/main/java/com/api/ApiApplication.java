package com.api;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(value = {"com.api.bank.repository", "com.api.auth.repository"})
@SpringBootApplication(scanBasePackages = {"com.api", "com.api.apk", "com.api.auth", "com.api.bank", "com.api.gateway", "com.api.gateway.shop", "com.api.gateway.tpe"})
public class ApiApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApiApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
