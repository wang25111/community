package com.mycoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author cj
 * @create 2021-12-24 16:28
 * 配置redis的数据序列化方式
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);

        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash 的 key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash 的 value 序列化方式
        template.setHashValueSerializer(RedisSerializer.string());

        template.afterPropertiesSet();

        return template;
    }
}
