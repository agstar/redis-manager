package com.redis.manage.controller;

import com.alibaba.fastjson.JSONArray;
import com.redis.manage.entity.Result;
import com.redis.manage.model.RedisServer;
import com.redis.manage.util.RedisServerUtil;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.redis.manage.util.Const.*;

@RestController
public class KeyController {
    private static final ScanOptions SCAN_OPTIONS = new ScanOptions.ScanOptionsBuilder().match("*").count(10000).build();

    @GetMapping("key/{serverName}/{dbIndex}")
    public Result getKeyList(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex) {
        StringRedisTemplate stringRedisTemplate = REDIS_TEMPLATE_MAP.get(serverName + DEFAULT_SEPARATOR + dbIndex);
        if (stringRedisTemplate == null) {
            Optional<RedisServer> first = REDIS_SERVER.stream().filter(x -> x.getName().equals(serverName)).findFirst();
            if (first.isPresent()) {
                stringRedisTemplate = RedisServerUtil.initRedisConnection(first.get(), dbIndex);
            } else {
                return Result.errorMsg("没有对应的服务");
            }
        }
        //scan 0 MATCH * COUNT 10000
        //加载初始化 template，点击server加载template
        Set<String> keys = stringRedisTemplate.execute((RedisCallback<Set<String>>) redisConnection -> {
            Set<String> binaryKeys = new HashSet<>();
            Cursor<byte[]> cursor = redisConnection.scan(SCAN_OPTIONS);
            while (cursor.hasNext()) {
                binaryKeys.add(new String(cursor.next()));
            }
            return binaryKeys;
        });
        JSONArray jsonArray = new JSONArray();
        if (keys != null) {
            keys.forEach(x -> RedisServerUtil.getKeyTree(x, jsonArray, serverName, dbIndex));
        }
        return Result.success("查询成功", jsonArray);
    }


    @GetMapping("key/{serverName}/{dbIndex}/{keyName}")
    public Result getKey(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @PathVariable("keyName") String keyName) {
        StringRedisTemplate stringRedisTemplate = REDIS_TEMPLATE_MAP.get(serverName + DEFAULT_SEPARATOR + dbIndex);
        if (stringRedisTemplate == null) {
            Optional<RedisServer> first = REDIS_SERVER.stream().filter(x -> x.getName().equals(serverName)).findFirst();
            if (first.isPresent()) {
                stringRedisTemplate = RedisServerUtil.initRedisConnection(first.get(), dbIndex);
            } else {
                return Result.errorMsg("没有对应的服务");
            }
        }
        DataType type = stringRedisTemplate.type(keyName);
        Map<String, Object> map = new HashMap<>();
        map.put("datatype", type);
        map.put("key", keyName);
        if (type != null) {
            switch (type) {
                case SET:
                    String s1 = stringRedisTemplate.opsForValue().get(keyName);
                    map.put("value", s1);
                    break;
                case HASH:
                    Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(keyName);
                    map.put("value", entries);
                    break;
                case LIST:
                    List<String> range = stringRedisTemplate.opsForList().range(keyName, 0, 1000);
                    map.put("value", range);
                    break;
                case STRING:
                    String s = stringRedisTemplate.opsForValue().get(keyName);
                    map.put("value", s);
                    break;
                case NONE:
                    break;
                case ZSET:
                    Set<String> range1 = stringRedisTemplate.opsForZSet().range(keyName, 0, 1000);
                    map.put("value", range1);
                    break;
                default:
                    break;
            }
        }
        return Result.success(map);
    }


    @GetMapping("key/{serverName}")
    public Result getKey(@PathVariable("serverName") String serverName) {
        Optional<RedisServer> first = REDIS_SERVER.stream().filter(x -> x.getName().equals(serverName)).findFirst();
        if (first.isPresent()) {
            List<Integer> count = RedisServerUtil.getRedisKeyCount(first.get());
            return Result.success("查询成功", count);
        } else {
            return Result.errorMsg("未找到" + serverName);
        }
    }

    @GetMapping("keyCount/{serverName}")
    public Result getKeyCount(@PathVariable("serverName") String serverName) {
        Optional<RedisServer> first = REDIS_SERVER.stream().filter(x -> x.getName().equals(serverName)).findFirst();
        if (first.isPresent()) {
            List<Integer> count = RedisServerUtil.getRedisKeyCount(first.get());
            return Result.success("查询成功", count);
        } else {
            return Result.errorMsg("未找到" + serverName);
        }
    }


}
