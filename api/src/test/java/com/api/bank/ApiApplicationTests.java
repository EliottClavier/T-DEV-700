package com.api.bank;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest
//@EnableJpaRepositories(value = {"com.api.bank.repository", "com.api.auth.repository", "com.api.gateway.tpe.repository"})

class ApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
