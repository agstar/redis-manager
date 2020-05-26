package com.redis.manager.handler;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * String类型处理器，
 * @author agstar
 */
@Component
public class StringValueHandler extends RedisValueHandler {



    @Override
    public Object getValue(RedisKey redisKey) {
        return null;
    }
}
