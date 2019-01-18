package com.redis.redismanage.controller;

import com.redis.redismanage.entity.Result;
import com.redis.redismanage.entity.StatusCode;
import com.redis.redismanage.model.RedisKey;
import com.redis.redismanage.model.RedisServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.redis.redismanage.util.Const.*;

@RestController
public class KeyController {


    @PutMapping("key/{id}/{oldkey}/{newkey}")
    public Result rename(String name) {
        //RedisTemplate
        return null;
    }

    @GetMapping("key")
    public Result getServerMenu() {
        Map<String, Map<String, List>> maps = new LinkedHashMap<>();
        for (RedisServer redisServer : REDIS_SERVER) {
            Map<String, List> map = new HashMap<>();
            for (int i = 0; i < 16; i++) {
                String key = redisServer.getName() + DEFAULT_SEPARATOR + i;
                CopyOnWriteArrayList<RedisKey> redisKeyList = REDIS_KEYS_LISTMAP.get(key);
                map.put("db" + i + "(" + redisKeyList.size() + ")", redisKeyList);
            }
            maps.put(redisServer.getName(), map);
        }
        return new Result(true, StatusCode.OK, "查询成功", maps);
    }




}
