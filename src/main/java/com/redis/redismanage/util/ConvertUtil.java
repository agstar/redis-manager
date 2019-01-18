package com.redis.redismanage.util;

import com.redis.redismanage.model.RedisKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConvertUtil {

    @SuppressWarnings("unchecked")
    public static List<RedisKey> getRedisKeyList(StringRedisTemplate stringRedisTemplate) {
        stringRedisTemplate.afterPropertiesSet();
        Set<String> keys = stringRedisTemplate.keys("*");

        List<RedisKey> redisKeyList = new ArrayList<>();
        keys.forEach(x -> {
            RedisKey redisKey = new RedisKey();
            redisKey.setKey(x);
            redisKey.setType(stringRedisTemplate.type(x));
            redisKeyList.add(redisKey);
        });
        return redisKeyList;
    }

}
