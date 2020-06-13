package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Base64;
import java.util.Optional;

/**
 * value处理器
 *
 * @author agstar
 * @date 2020/4/23 9:35
 */
public interface RedisValueHandler {
    
    /**
     * 
     *
     * @author agstar
     * @date 2020/4/23 9:25
     */
    Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate);

    /**
     * 获取value，只依据base64编码的key
     *
     * @author agstar
     * @date 2020/4/23 9:20
     */
    default Object getValue(String base64keyName, StringRedisTemplate stringRedisTemplate) {
        RedisKey redisKey = stringRedisTemplate.execute((RedisCallback<RedisKey>) redisConnection -> {
            byte[] decode = Base64.getDecoder().decode(base64keyName);
            DataType type = redisConnection.type(decode);
            return RedisKey.builder().base64KeyName(base64keyName)
                    .type(Optional.ofNullable(type)
                            .map(DataType::code)
                            .orElse(null)
                    )
                    .build();
        });
        return getValue(redisKey, stringRedisTemplate);
    }

    Object getValue(RedisKey redisKey);



}
