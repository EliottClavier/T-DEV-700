package com.api.gateway.config;

import com.api.gateway.controller.RedisExpirationListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@EnableAutoConfiguration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, String> customRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    RedisMessageListenerContainer container(
            RedisConnectionFactory connectionFactory,
            RedisExpirationListener mesageListener
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // Message listener on Redis key expiration (allows to handle transaction timeout)
        container.addMessageListener(
                mesageListener,
                new PatternTopic("__keyevent@*__:expired")
        );
        return container;
    }
}
