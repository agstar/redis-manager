package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import com.redis.manager.util.RedisServerUtil;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author agstar
 */
@Component
public class ListValueHandler implements RedisValueHandler {
    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {
        return null;
    }

    @Override
    public Object getValue(RedisKey redisKey) {
        long start = 0L;
        long end = 1000L;
        //根据名称查询
        StringRedisTemplate stringRedisTemplate = RedisServerUtil.getStringRedisTemplate(redisKey.getServerName(), redisKey.getDbIndex());
        List<String> stringList = stringRedisTemplate.opsForList().range(redisKey.getKeyName(), start, end);
        if (CollectionUtils.isEmpty(stringList)) {
            return stringList;
        }
        //如果没有，根据base64编码的key查询
        return stringRedisTemplate.execute((RedisCallback<List<String>>) redisConnection -> {
            byte[] key = Base64.getDecoder().decode(redisKey.getBase64KeyName());
            List<byte[]> byteList = redisConnection.lRange(key, start, end);
            if (byteList == null) {
                return null;
            }
            return byteList.stream().map(b -> new String(b, StandardCharsets.UTF_8)).collect(Collectors.toList());
        });

    }
}
