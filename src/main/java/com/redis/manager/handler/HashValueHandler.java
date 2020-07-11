package com.redis.manager.handler;

import com.redis.manager.model.RedisKey;
import com.redis.manager.util.RedisServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author agstar
 * @date 2020/6/13 15:49
 */
@Slf4j
@Component
public class HashValueHandler implements RedisValueHandler {
    private static final ScanOptions SCAN_OPTIONS = new ScanOptions.ScanOptionsBuilder().match("*").count(10000).build();

    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {


        return null;
    }

    @Override
    public Object getValue(RedisKey redisKey) {
        //根据名称查询
        StringRedisTemplate stringRedisTemplate = RedisServerUtil
                .getStringRedisTemplate(redisKey.getServerName(), redisKey.getDbIndex());
        Boolean exist = stringRedisTemplate.hasKey(redisKey.getKeyName());
        boolean exists = Optional.ofNullable(exist).orElse(false);
        if (exists) {
            try (Cursor<Map.Entry<Object, Object>> entryCursor
                         = stringRedisTemplate.opsForHash().scan(redisKey.getHashKey(), SCAN_OPTIONS)) {
                long size = stringRedisTemplate.opsForHash().size(redisKey.getKeyName());
                return getObject(entryCursor, size);
            } catch (IOException e) {
                log.error("连接异常", e);
                throw new RuntimeException("连接异常");
            }
        } else {
            //如果不能直接通过keyname获取值，通过base64编码的key重新获取
            byte[] byteKeyName = Base64.getDecoder().decode(redisKey.getBase64KeyName());
            stringRedisTemplate.execute((RedisCallback<Map<String, String>>) connection -> {
                Boolean existsBool = connection.exists(byteKeyName);
                boolean hashExists = Optional.ofNullable(existsBool).orElse(false);
                if (hashExists) {
                    Cursor<Map.Entry<byte[], byte[]>> entryCursor = connection.hScan(byteKeyName, SCAN_OPTIONS);
                    Long aLong = connection.hLen(byteKeyName);
                    if (aLong == null) {
                        return null;
                    }
                    int mapSize = aLong > 1000 ? 1334 : (int) (aLong * 2);
                    Map<String, String> map = new HashMap<>(mapSize);
                    while (entryCursor.hasNext()) {
                        Map.Entry<byte[], byte[]> next = entryCursor.next();
                        map.put(new String(next.getKey(), StandardCharsets.UTF_8), new String(next.getValue(), StandardCharsets.UTF_8));
                    }
                    return map;
                }
                return null;
            });
        }
        return null;
    }

    @Override
    public void saveKey(RedisKey redisKey) {
        //根据名称查询
        StringRedisTemplate stringRedisTemplate = RedisServerUtil
                .getStringRedisTemplate(redisKey.getServerName(), redisKey.getDbIndex());
        stringRedisTemplate.opsForHash().put(redisKey.getKeyName(), redisKey.getHashKey(), redisKey.getKeyValue().toString());
    }

    private Object getObject(Cursor<Map.Entry<Object, Object>> entryCursor, long size) {
        int mapSize = size > 1000 ? 1334 : (int) (size * 2);
        Map<String, String> map = new HashMap<>(mapSize);
        while (entryCursor.hasNext()) {
            Map.Entry<Object, Object> next = entryCursor.next();
            map.put(next.getKey().toString(), next.getValue().toString());
        }
        return map;
    }
}
