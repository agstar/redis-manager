package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ZsetValueHandler implements RedisValueHandler{
    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {
        return null;
    }
}
