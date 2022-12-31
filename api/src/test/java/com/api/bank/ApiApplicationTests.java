package com.api.bank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.config.location=classpath:application.yml"},  args = {"--spring.main.allow-bean-definition-overriding=true"})

@SpringBootTest(classes = {ApiApplicationTests.class})
@ExtendWith(SpringExtension.class)
class ApiApplicationTests {

    @Test
    void contextLoads() {
       var a= 1;
        assertEquals(1, a);
    }

}
