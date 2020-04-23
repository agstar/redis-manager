package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * String类型处理器，
 * @author agstar
 */
public class StringValueHandler implements RedisValueHandler {
    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {
        return null;
    }

    @Override
    public String getValue(String base64keyName, StringRedisTemplate stringRedisTemplate) {
        return stringRedisTemplate.execute((RedisCallback<String>) redisConnection -> {
            byte[] key = Base64.getDecoder().decode(base64keyName);
            byte[] bytes = redisConnection.get(key);
            return Optional.ofNullable(bytes).map((s) -> {
                return new String(bytes, StandardCharsets.UTF_8);
            }).orElse(null);
        });
    }
}
