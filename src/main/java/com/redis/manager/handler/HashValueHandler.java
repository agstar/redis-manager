package com.redis.manager.handler;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
@Component
public class HashValueHandler implements RedisValueHandler {

    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {

        Map<Object, Object> value = stringRedisTemplate.opsForHash().entries(redisKey.getKeyName());
        //
        if(!CollectionUtils.isEmpty(value)) {
            Long expire = stringRedisTemplate.getExpire(redisKey.getKeyName());
            return  RedisKey.builder().keyValue(value).ttl(Optional.ofNullable(expire).orElse(-1L)).build();
        }else{
            //如果不能直接通过keyname获取值，通过base64编码的key再获取一次
            byte[] byteKeyName = Base64.getDecoder().decode(redisKey.getBase64KeyName());
            stringRedisTemplate.execute((RedisCallback<RedisKey>) connection->{
                byte[] bytes = connection.get(byteKeyName);
                Long ttl = connection.ttl(byteKeyName);
                String byteValue = Optional.ofNullable(bytes).map(String::new).orElse(null);
                return  RedisKey.builder().keyValue(byteValue).ttl(Optional.ofNullable(ttl).orElse(-1L)).build();
            });
        }
        Optional.empty();
        return null;
    }

    @Override
    public Object getValue(RedisKey redisKey) {
        return null;
    }
}
