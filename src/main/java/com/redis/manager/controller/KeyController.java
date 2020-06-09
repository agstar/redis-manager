package com.redis.manager.controller;

import com.alibaba.fastjson.JSONArray;
import com.redis.manager.entity.Result;
import com.redis.manager.handle.RedisContextHolder;
import com.redis.manager.model.RedisKey;
import com.redis.manager.model.RedisServer;
import com.redis.manager.util.RedisServerUtil;
import com.redis.manager.util.RedisUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.redis.manager.util.Const.*;

/**
 * @author agstar
 */
@RestController
@AllArgsConstructor
public class KeyController {
    private static final ScanOptions SCAN_OPTIONS = new ScanOptions.ScanOptionsBuilder().match("*").count(10000).build();
    private final RedisContextHolder redisContextHolder;

    /**
     * 获取dbindex中的所有key
     *
     * @author agstar
     * @date 2019/6/13 21:13
     */
    @GetMapping("key/{serverName}/{dbIndex}")
    public Result<JSONArray> getKeyList(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex) {
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate(serverName, dbIndex);
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
        JSONArray keyTree = RedisServerUtil.getKeyTree(keys, serverName, dbIndex);
        return Result.success(keyTree);
    }

    private StringRedisTemplate getStringRedisTemplate(String serverName, int dbIndex) {
        StringRedisTemplate stringRedisTemplate = REDIS_TEMPLATE_MAP.get(serverName + DEFAULT_SEPARATOR + dbIndex);
        if (stringRedisTemplate == null) {
            Optional<RedisServer> first = REDIS_SERVER.stream().filter(x -> x.getName().equals(serverName)).findFirst();
            if (first.isPresent()) {
                return RedisServerUtil.initRedisConnection(first.get(), dbIndex);
            }
        }
        throw new RuntimeException("初始化StringRedisTemplate失败");
    }

    /**
     * 获取value的值
     *
     * @author agstar
     * @date 2019/6/13 21:14
     */
    @GetMapping("value/{serverName}/{dbIndex}/{type}/{base64keyName}")
    public Result<Object> getValue(@PathVariable("serverName") String serverName,
                           @PathVariable("dbIndex") int dbIndex,
                           @PathVariable("type") String type,
                           @PathVariable("base64keyName") String base64keyName) {
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate(serverName, dbIndex);
        Object value = redisContextHolder.getHandler(type).getValue(base64keyName, stringRedisTemplate);
        return Result.success(value);
    }

    /**
     * 获取16个数据库中的key数量
     *
     * @author agstar
     * @date 2019/6/13 22:37
     */
    @GetMapping("keyCount/{serverName}")
    public Result getKeyCount(@PathVariable("serverName") String serverName) {
        Optional<RedisServer> first = REDIS_SERVER.stream().filter(x -> x.getName().equals(serverName)).findFirst();
        if (first.isPresent()) {
            List<Integer> count = RedisServerUtil.getRedisKeyCount(first.get());
            return Result.success(count);
        } else {
            return Result.errorMsg("未找到" + serverName);
        }
    }


    @PostMapping("key/{serverName}/{dbIndex}")
    public Result addKey(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @RequestBody RedisKey redisKey) {
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate(serverName, dbIndex);
        DataType type = DataType.fromCode(redisKey.getType());
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(stringRedisTemplate);
        switch (type) {
            case SET:
                redisUtil.sAdd(redisKey.getKeyName(), redisKey.getKeyValue().toString());
                break;
            case HASH:
                redisUtil.hPut(redisKey.getKeyName(), redisKey.getHashKey(), redisKey.getKeyValue().toString());
                break;
            case LIST:
                redisUtil.lRightPush(redisKey.getKeyName(), redisKey.getKeyValue().toString());
                break;
            case STRING:
                redisUtil.set(redisKey.getKeyName(), redisKey.getKeyValue().toString());
                break;
            case ZSET:
                redisUtil.zAdd(redisKey.getKeyName(), redisKey.getKeyValue().toString(), redisKey.getScore());
                break;
            default:
                return Result.errorMsg("没有找到对应的类型");
        }
        return Result.success();
    }


    @PostMapping("key/delete/{serverName}/{dbIndex}")
    public Result<Void> deleteKey(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @RequestBody List<String> keyList) {
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate(serverName, dbIndex);
        if (stringRedisTemplate == null) {
            return Result.errorMsg("获取StringRedisTemplate失败");
        }
        keyList.forEach(stringRedisTemplate::delete);
        return Result.success();
    }

    @PutMapping("key/{serverName}/{dbIndex}/{oldKeyName}/{newKeyName}")
    public Result<Void> rename(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @PathVariable("oldKeyName") String oldKeyName, @PathVariable("newKeyName") String newKeyName) {
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate(serverName, dbIndex);
        stringRedisTemplate.rename(oldKeyName, newKeyName);
        return Result.success();
    }

    @PutMapping("key/ttl/{serverName}/{dbIndex}/{keyName}/{ttl}")
    public Result<Void> updateTtl(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @PathVariable("keyName") String keyName, @PathVariable("ttl") Long ttl) {
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate(serverName, dbIndex);
        Boolean expire = stringRedisTemplate.expire(keyName, ttl, TimeUnit.SECONDS);
        if (expire != null && expire) {
            return Result.success();
        } else {
            return Result.errorMsg("设置ttl失败");
        }

    }


}
