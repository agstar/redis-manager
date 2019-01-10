package com.redis.redismanage.util;

import com.redis.redismanage.model.RedisKey;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConvertUtil {

    @SuppressWarnings("unchecked")
    public static List<RedisKey> getRedisKeyList(RedisTemplate redisTemplate) {
        Set keys = redisTemplate.keys("*");
        List<RedisKey> redisKeyList = new ArrayList<>();
        if (keys != null) {
            keys.forEach(x -> {
                RedisKey redisKey = new RedisKey();
                redisKey.setKey(x.toString());
                redisKey.setType(redisTemplate.type(x));
                redisKeyList.add(redisKey);
            });
        }
        return redisKeyList;
    }

}
