package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Base64;
import java.util.Optional;

public class DefaultValueHander  implements RedisValueHandler {


    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {
        return null;
    }

    /*public Object getValue(String base64keyName,StringRedisTemplate stringRedisTemplate) {
        RedisKey redisKey = getRedisKey(base64keyName, stringRedisTemplate);

        return null;
    }

    public RedisKey getRedisKey(String base64keyName,StringRedisTemplate stringRedisTemplate){
       return stringRedisTemplate.execute((RedisCallback<RedisKey>) redisConnection -> {
            byte[] decode = Base64.getDecoder().decode(base64keyName);
            DataType type = redisConnection.type(decode);
//            byte[] bytes = redisConnection.get(decode);
            return   RedisKey.builder().base64KeyName(base64keyName)
                    .type(Optional.ofNullable(type)
                            .map(DataType::code)
                            .orElse(null)
                    )
                    .build();
        });
    }*/

}
