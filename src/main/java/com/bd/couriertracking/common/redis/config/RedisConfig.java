package com.bd.couriertracking.common.redis.config;

import com.bd.couriertracking.common.redis.repository.RedisRepository.LastLoc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory cf,
            ObjectMapper redisObjectMapper
    ) {
        var t = new RedisTemplate<String, Object>();
        t.setConnectionFactory(cf);
        var keySer = new StringRedisSerializer();
        var valSer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);
        t.setKeySerializer(keySer);
        t.setValueSerializer(valSer);
        t.setHashKeySerializer(keySer);
        t.setHashValueSerializer(valSer);
        t.afterPropertiesSet();
        return t;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }

    @Bean(name = "lastLocRedisTemplate")
    public RedisTemplate<String, LastLoc> lastLocRedisTemplate(RedisConnectionFactory cf) {
        var tpl = new RedisTemplate<String, LastLoc>();
        tpl.setConnectionFactory(cf);

        var keySer = new StringRedisSerializer();
        var valSer = new Jackson2JsonRedisSerializer<>(LastLoc.class);

        tpl.setKeySerializer(keySer);
        tpl.setHashKeySerializer(keySer);
        tpl.setValueSerializer(valSer);
        tpl.setHashValueSerializer(valSer);

        tpl.afterPropertiesSet();
        return tpl;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf) {
        return RedisCacheManager.builder(cf).build();
    }
}
