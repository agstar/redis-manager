package com.redis.redismanage.util;

import com.redis.redismanage.model.RedisKey;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ConvertUtil {

    @SuppressWarnings("unchecked")
    public static List<RedisKey> getRedisKeyList(StringRedisTemplate stringRedisTemplate) {
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
//        redisScript.setScriptText("info");

//        Object execute = stringRedisTemplate.execute(redisScript, new ArrayList<>(), "");
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
