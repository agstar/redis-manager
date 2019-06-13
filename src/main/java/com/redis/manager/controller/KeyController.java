package com.redis.manager.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redis.manager.entity.Result;
import com.redis.manager.entity.StatusCode;
import com.redis.manager.model.RedisKey;
import com.redis.manager.model.RedisServer;
import com.redis.manager.util.RedisServerUtil;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.redis.manager.util.Const.*;

@RestController
public class KeyController {
    private static final ScanOptions SCAN_OPTIONS = new ScanOptions.ScanOptionsBuilder().match("*").count(10000).build();

    /**
     * 获取dbindex中的所有key
     *
     * @author agstar
     * @date 2019/6/13 21:13
     */
    @GetMapping("key/{serverName}/{dbIndex}")
    public Result getKeyList(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex) {
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate(serverName, dbIndex);
        //scan 0 MATCH * COUNT 10000
        //加载初始化 template，点击server加载template
        Set<RedisKey> keys = stringRedisTemplate.execute((RedisCallback<Set<RedisKey>>) redisConnection -> {
            Set<RedisKey> binaryKeys = new HashSet<>();
            Cursor<byte[]> cursor = redisConnection.scan(SCAN_OPTIONS);
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                DataType type = stringRedisTemplate.type(key);
                binaryKeys.add(new RedisKey(key, type));
            }
            return binaryKeys;
        });
        JSONArray jsonArray = new JSONArray();
        if (keys != null) {
            keys.forEach(x -> RedisServerUtil.getKeyTree(x, jsonArray, serverName, dbIndex));
        }
        return new Result(true, StatusCode.OK, "查询成功", jsonArray);
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
    @GetMapping("key/{serverName}/{dbIndex}/{keyName}")
    public Result getKey(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @PathVariable("keyName") String keyName) {
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate(serverName, dbIndex);
        DataType type = stringRedisTemplate.type(keyName);
        Object o = null;
        if (type != null) {
            switch (type) {
                case SET:
                    o = stringRedisTemplate.opsForSet().pop(keyName);
                    break;
                case HASH:
                    o = stringRedisTemplate.opsForHash().entries(keyName);
                    break;
                case LIST:
                    o = stringRedisTemplate.opsForList().range(keyName, 0, 1000);
                    break;
                case STRING:
                    o = stringRedisTemplate.opsForValue().get(keyName);
                    break;
                case NONE:
                    break;
                case ZSET:
                    o = stringRedisTemplate.opsForZSet().range(keyName, 0, 1000);
                    break;
                default:
                    break;
            }
        }
        return new Result(true, StatusCode.OK, "查询成功", o);
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
            return new Result(true, StatusCode.OK, "查询成功", count);
        } else {
            return new Result(false, StatusCode.ERROR, "未找到" + serverName);
        }
    }

    @PostMapping("key/{serverName}/{dbIndex}")
    public Result addKey(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @RequestBody RedisServer redisServer) {
        return null;
    }


}
