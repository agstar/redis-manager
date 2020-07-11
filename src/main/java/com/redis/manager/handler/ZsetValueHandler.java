package com.redis.manager.handler;

import com.redis.manager.model.RedisKey;
import com.redis.manager.util.RedisServerUtil;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author agstar
 */
@Component
public class ZsetValueHandler implements RedisValueHandler {
    private static final ScanOptions SCAN_OPTIONS = new ScanOptions.ScanOptionsBuilder().match("*").count(10000).build();

    @Override
    public Object getValue(RedisKey redisKey, StringRedisTemplate stringRedisTemplate) {
        return null;
    }

    @Override
    public Object getValue(RedisKey redisKey) {
        //根据名称查询
        /*StringRedisTemplate stringRedisTemplate = RedisServerUtil.getStringRedisTemplate(redisKey.getServerName(), redisKey.getDbIndex());
        Boolean aBoolean = stringRedisTemplate.hasKey(redisKey.getKeyName());
        boolean exist = Optional.ofNullable(aBoolean).orElse(false);
        if (exist) {
           stringRedisTemplate.opsForZSet().rangeByScoreWithScores()
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
        }*/
        return null;
    }

    @Override
    public void saveKey(RedisKey redisKey) {
        //根据名称查询
        StringRedisTemplate stringRedisTemplate = RedisServerUtil
                .getStringRedisTemplate(redisKey.getServerName(), redisKey.getDbIndex());
        stringRedisTemplate.opsForZSet().add(redisKey.getKeyName(), redisKey.getKeyValue().toString(), redisKey.getScore());
    }
}
