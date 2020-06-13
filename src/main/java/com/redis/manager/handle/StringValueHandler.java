package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import com.redis.manager.util.RedisServerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * String类型处理器，
 *
 * @author agstar
 */
@Component
public class StringValueHandler implements RedisValueHandler {
    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {
        return null;
    }

    @Override
    public String getValue(String base64keyName, StringRedisTemplate stringRedisTemplate) {
        return stringRedisTemplate.execute((RedisCallback<String>) redisConnection -> {
            byte[] key = Base64.getDecoder().decode(base64keyName);
            byte[] bytes = redisConnection.get(key);
            return Optional.ofNullable(bytes).map((s) -> {
                return new String(bytes, StandardCharsets.UTF_8);
            }).orElse(null);
        });
    }

    @Override
    public Object getValue(RedisKey redisKey) {
        //根据名称查询
        StringRedisTemplate stringRedisTemplate = RedisServerUtil.getStringRedisTemplate(redisKey.getServerName(), redisKey.getDbIndex());
        String value = stringRedisTemplate.opsForValue().get(redisKey.getKeyName());
        if (StringUtils.isNotEmpty(value)) {
            return value;
        }
        //如果没有，根据base64编码的key查询
        return stringRedisTemplate.execute((RedisCallback<String>) redisConnection -> {
            byte[] key = Base64.getDecoder().decode(redisKey.getBase64KeyName());
            byte[] bytes = redisConnection.get(key);
            return Optional.ofNullable(bytes).map(s -> new String(s, StandardCharsets.UTF_8)).orElse(null);
        });
    }
}
