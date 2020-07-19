package com.redis.manager.handler;

import com.alibaba.fastjson.JSONArray;
import com.redis.manager.model.RedisKey;
import com.redis.manager.util.RedisServerUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.redis.manager.util.Const.REDIS_SERVER;

/**
 * @author agstar
 * @date 2020/7/11 16:05
 */
@Component
public class KeyHandler {
    private static final ScanOptions SCAN_OPTIONS = new ScanOptions.ScanOptionsBuilder().match("*").count(10000).build();

    public JSONArray keys(RedisKey redisKey) {
        //根据名称查询
        StringRedisTemplate stringRedisTemplate = RedisServerUtil
                .getStringRedisTemplate(redisKey.getServerName(), redisKey.getDbIndex());
        //scan 0 MATCH * COUNT 10000
        //加载初始化 template，点击server加载template
        Set<RedisKey> keys = stringRedisTemplate.execute((RedisCallback<Set<RedisKey>>) redisConnection -> {
            Set<RedisKey> binaryKeys = new HashSet<>();
            Cursor<byte[]> cursor = redisConnection.scan(SCAN_OPTIONS);
            while (cursor.hasNext()) {
                byte[] next = cursor.next();
                String key = new String(next, StandardCharsets.UTF_8);
                String base64Key = Base64.getEncoder().encodeToString(next);
                String type = Optional.ofNullable(stringRedisTemplate.type(key)).map(DataType::code).orElse(null);
                binaryKeys.add(RedisKey.builder().keyName(key).type(type).base64KeyName(base64Key).build());
            }
            return binaryKeys;
        });
        JSONArray keyTree = RedisServerUtil.getKeyTree(keys, redisKey.getServerName(), redisKey.getDbIndex());
        return keyTree;
    }

    public List<Integer> keyCount(String serverName) {
        return REDIS_SERVER.stream()
                .filter(x -> x.getName().equals(serverName))
                .findFirst()
                .map(RedisServerUtil::getRedisKeyCount)
                .orElseThrow(() -> new RuntimeException("未找到" + serverName));
    }

    public void deleteKey(String serverName, int dbIndex, List<String> keyList) {
        //根据名称查询
        StringRedisTemplate stringRedisTemplate = RedisServerUtil
                .getStringRedisTemplate(serverName, dbIndex);
        keyList.forEach(stringRedisTemplate::delete);
    }

    public void renameKey(RedisKey redisKey, String newKeyName) {
        //根据名称查询
        StringRedisTemplate stringRedisTemplate = RedisServerUtil
                .getStringRedisTemplate(redisKey.getServerName(), redisKey.getDbIndex());
        stringRedisTemplate.rename(redisKey.getKeyName(), newKeyName);
    }

    public boolean updateTtl(String serverName, int dbIndex, String keyName, Long ttl) {
        //根据名称查询
        StringRedisTemplate stringRedisTemplate = RedisServerUtil
                .getStringRedisTemplate(serverName, dbIndex);
        Boolean expire = stringRedisTemplate.expire(keyName, ttl, TimeUnit.SECONDS);
        return (expire != null && expire);

    }

}
