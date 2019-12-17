package com.jisen.seckill.beanautoconfig;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.apache.dubbo.common.serialize.Serialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 自定义redis连接池，springboot自带连接池默认使用redistemplate，如果自定义使用使用jedis，则即可使用redistemplate又可使用jedis
 *
 * @author jisen
 * @date 2019/6/14 21:19
 */
@Configuration
//@EnableCaching
public class RedisAutoConfig {
    //@Primary
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        //template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
