package com.redis.redismanage.controller;

import com.redis.redismanage.entity.Result;
import com.redis.redismanage.entity.StatusCode;
import com.redis.redismanage.model.RedisServer;
import com.redis.redismanage.util.RedisServerUtil;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.redis.redismanage.util.Const.*;

@RestController
public class KeyController {
    private ScanOptions build = new ScanOptions.ScanOptionsBuilder().match("*").count(10000).build();

    @GetMapping("key/{serverName}/{dbIndex}")
    public Result getKey(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex) {
        StringRedisTemplate stringRedisTemplate = REDIS_TEMPLATE_MAP.get(serverName + DEFAULT_SEPARATOR + dbIndex);
        //scan 0 MATCH * COUNT 10000
        //加载初始化 template，点击server加载template
        Set<Object> keys = stringRedisTemplate.execute((RedisCallback<Set<Object>>) redisConnection -> {
            Set<Object> binaryKeys = new HashSet<>();
            Cursor<byte[]> cursor = redisConnection.scan(build);
            //Properties info = redisConnection.info();
            while (cursor.hasNext()) {
                binaryKeys.add(new String(cursor.next()));
            }
            return binaryKeys;
        });
        return new Result(true, StatusCode.OK, "查询成功", keys);
    }


    @GetMapping("key/{serverName}")
    public Result getKeyCount(@PathVariable("serverName") String serverName) {
        Optional<RedisServer> first = REDIS_SERVER.stream().filter(x -> x.getName().equals(serverName)).findFirst();
        if (first.isPresent()) {
            List<Integer> count = RedisServerUtil.initRedisConnection(first.get());
            return new Result(true, StatusCode.OK, "查询成功", count);
        } else {
            return new Result(false, StatusCode.ERROR, "未找到" + serverName);
        }
    }


}
