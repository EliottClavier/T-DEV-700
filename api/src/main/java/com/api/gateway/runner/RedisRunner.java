package com.api.gateway.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TPE;
import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TRANSACTION;

@Component
public class RedisRunner implements ApplicationRunner {

    private final RedisTemplate<String, String> customRedisTemplate;

    @Autowired
    public RedisRunner(
            RedisTemplate<String, String> customRedisTemplate
    ) {
        this.customRedisTemplate = customRedisTemplate;
    }

    // At the start of the application, the Redis database is cleared
    public void run(ApplicationArguments args) {
        // Delete all Transaction Requests in Redis
        customRedisTemplate.delete(HASH_KEY_NAME_TRANSACTION);

        // Delete all TPE in Redis
        customRedisTemplate.delete(HASH_KEY_NAME_TPE);
    }

}
