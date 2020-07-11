package com.redis.manager.handler;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.core.StringRedisTemplate;

public class DefaultValueHandler implements RedisValueHandler {


    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {
        return null;
    }

    @Override
    public Object getValue(RedisKey redisKey) {
        throw new RuntimeException("unsupported type["+redisKey.getType()+"]");
    }

    @Override
    public void saveKey(RedisKey redisKey) {
        throw new RuntimeException("unsupported type["+redisKey.getType()+"]");
    }
}
