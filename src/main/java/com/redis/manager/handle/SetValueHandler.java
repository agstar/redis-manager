package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;
@Component
public class SetValueHandler implements RedisValueHandler {

    @Override
    public Object getValue(RedisKey redisKey,StringRedisTemplate stringRedisTemplate) {
        String value = stringRedisTemplate.opsForSet().pop(redisKey.getKeyName());
        //
        if(StringUtils.isNotBlank(value)) {
            Long expire = stringRedisTemplate.getExpire(redisKey.getKeyName());
            return  RedisKey.builder().keyValue(value).ttl(Optional.ofNullable(expire).orElse(-1L)).build();
        }else{
            //如果不能直接通过keyname获取值，通过base64编码的key重新获取
            byte[] byteKeyName = Base64.getDecoder().decode(redisKey.getBase64KeyName());
            stringRedisTemplate.execute((RedisCallback<RedisKey>)connection->{
                byte[] bytes = connection.get(byteKeyName);
                Long ttl = connection.ttl(byteKeyName);
                String byteValue = Optional.ofNullable(bytes).map(String::new).orElse(null);
                return  RedisKey.builder().keyValue(byteValue).ttl(Optional.ofNullable(ttl).orElse(-1L)).build();
            });
        }



        return null;
    }
}
