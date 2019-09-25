package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.core.StringRedisTemplate;

public class ListValueHandler implements RedisValueHandler{
    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {
        return null;
    }
}
