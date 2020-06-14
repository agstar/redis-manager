package com.redis.manager.handler;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ListValueHandler implements RedisValueHandler{
    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {
        return null;
    }

    @Override
    public Object getValue(RedisKey redisKey) {
        return null;
    }
}
