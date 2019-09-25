package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.core.StringRedisTemplate;

public interface RedisValueHandler {

    Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate);


}
