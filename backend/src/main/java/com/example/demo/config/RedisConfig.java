package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * <p>
 * 配置 RedisTemplate 的序列化方式，使用 JSON 替代默认的 JDK 序列化。
 * 这样在 Redis 客户端（如 Redis Desktop Manager）中可以直接查看可读的数据。
 * </p>
 */
@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate
     * <p>
     * Key 使用 String 序列化，Value 使用 Jackson JSON 序列化。
     * </p>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key 序列化器：String
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value 序列化器：Jackson JSON（使用通用序列化器，兼容性更好）
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * 配置 StringRedisTemplate（简化字符串操作）
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
