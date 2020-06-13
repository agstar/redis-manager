package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import com.redis.manager.util.RedisServerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author agstar
 */
@Component
public class SetValueHandler implements RedisValueHandler {
    private static final ScanOptions SCAN_OPTIONS = new ScanOptions.ScanOptionsBuilder().match("*").count(10000).build();

    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {
        String value = stringRedisTemplate.opsForSet().pop(redisKey.getKeyName());
        //
        if (StringUtils.isNotBlank(value)) {
            Long expire = stringRedisTemplate.getExpire(redisKey.getKeyName());
            return RedisKey.builder().keyValue(value).ttl(Optional.ofNullable(expire).orElse(-1L)).build();
        } else {
            //如果不能直接通过keyname获取值，通过base64编码的key重新获取
            byte[] byteKeyName = Base64.getDecoder().decode(redisKey.getBase64KeyName());
            stringRedisTemplate.execute((RedisCallback<RedisKey>) connection -> {
                byte[] bytes = connection.get(byteKeyName);
                Long ttl = connection.ttl(byteKeyName);
                String byteValue = Optional.ofNullable(bytes).map(String::new).orElse(null);
                return RedisKey.builder().keyValue(byteValue).ttl(Optional.ofNullable(ttl).orElse(-1L)).build();
            });
        }


        return null;
    }

    @Override
    public Object getValue(RedisKey redisKey) {
        //根据名称查询
        StringRedisTemplate stringRedisTemplate = RedisServerUtil.getStringRedisTemplate(redisKey.getServerName(), redisKey.getDbIndex());
        Boolean aBoolean = stringRedisTemplate.hasKey(redisKey.getKeyName());
        boolean exist = Optional.ofNullable(aBoolean).orElse(false);
        if (exist) {
            Cursor<String> scan = stringRedisTemplate.opsForSet().scan(redisKey.getKeyName(), SCAN_OPTIONS);
            Long size = stringRedisTemplate.opsForSet().size(redisKey.getKeyName());
            if (size == null) {
                return null;
            }
            int setSize = size > 1000 ? 1334 : (int) (size * 2);
            Set<String> set = new HashSet<>(setSize);
            while (scan.hasNext()) {
                String next = scan.next();
                set.add(next);
            }
            return set;
        } else {
            byte[] byteKeyName = Base64.getDecoder().decode(redisKey.getBase64KeyName());
            //如果没有，根据base64编码的key查询
            return stringRedisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Boolean existsBool = connection.exists(byteKeyName);
                boolean existKey = Optional.ofNullable(existsBool).orElse(false);
                if (existKey) {
                    Long aLong = connection.sCard(byteKeyName);
                    if (aLong == null) {
                        return null;
                    }
                    int setSize = aLong > 1000 ? 1334 : (int) (aLong * 2);
                    Cursor<byte[]> cursor = connection.sScan(byteKeyName, SCAN_OPTIONS);
                    Set<String> set = new HashSet<>(setSize);
                    while (cursor.hasNext()) {
                        byte[] next = cursor.next();
                        set.add(new String(next, StandardCharsets.UTF_8));
                    }
                    return set;
                }
                return null;
            });
        }
    }
}
