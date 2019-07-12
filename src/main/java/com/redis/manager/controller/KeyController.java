package com.redis.manager.controller;

import com.alibaba.fastjson.JSONArray;
import com.redis.manager.entity.Result;
import com.redis.manager.model.RedisKey;
import com.redis.manager.model.RedisServer;
import com.redis.manager.util.RedisServerUtil;
import com.redis.manager.util.RedisUtil;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
                String type = Optional.ofNullable(stringRedisTemplate.type(key)).map(DataType::code).orElse(null);
                binaryKeys.add(RedisKey.builder().keyName(key).type(type).build());
            }
            return binaryKeys;
        });
        JSONArray jsonArray = new JSONArray();
        if (keys != null) {
            keys.forEach(x -> RedisServerUtil.getKeyTree(x, jsonArray, serverName, dbIndex));
        }
        return Result.success("查询成功", jsonArray);
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
        Object value = null;
        if (type != null) {
            switch (type) {
                case SET:
                    value = stringRedisTemplate.opsForSet().pop(keyName);
                    break;
                case HASH:
                    value = stringRedisTemplate.opsForHash().entries(keyName);
                    break;
                case LIST:
                    value = stringRedisTemplate.opsForList().range(keyName, 0, 1000);
                    break;
                case STRING:
                    value = stringRedisTemplate.opsForValue().get(keyName);
                    break;
                case NONE:
                    break;
                case ZSET:
                    value = stringRedisTemplate.opsForZSet().range(keyName, 0, 1000);
                    break;
                default:
                    break;
            }
        }
        return Result.success("查询成功", value);
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
            return Result.success("查询成功", count);
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
                redisUtil.sAdd(redisKey.getKeyName(), redisKey.getKeyValue());
                break;
            case HASH:
                redisUtil.hPut(redisKey.getKeyName(), redisKey.getHashKey(), redisKey.getKeyValue());
                break;
            case LIST:
                redisUtil.lRightPush(redisKey.getKeyName(), redisKey.getKeyValue());
                break;
            case STRING:
                redisUtil.set(redisKey.getKeyName(), redisKey.getKeyValue());
                break;
            case ZSET:
                redisUtil.zAdd(redisKey.getKeyName(), redisKey.getKeyValue(), redisKey.getScore());
                break;
            default:
                return Result.errorMsg("没有找到对应的类型");
        }
        return Result.success("添加成功");
    }

    @PostMapping("key/delete/{serverName}/{dbIndex}")
    public Result deleteKey(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @RequestBody List<String> keyList) {
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate(serverName, dbIndex);
        if (stringRedisTemplate == null) {
            return Result.errorMsg("获取StringRedisTemplate失败");
        }
        keyList.forEach(stringRedisTemplate::delete);
        return Result.success("删除成功");
    }


}
