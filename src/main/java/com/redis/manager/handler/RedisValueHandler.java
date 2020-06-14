package com.redis.manager.handler;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * value处理器
 *
 * @author agstar
 * @date 2020/4/23 9:35
 */
public interface RedisValueHandler {

    Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate);

    /**
     * @author agstar
     * @date 2020/4/23 9:25
     */
    Object getValue(RedisKey redisKey);

}
