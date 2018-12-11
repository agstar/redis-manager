package com.redis.redismanage.controller;

import com.redis.redismanage.entity.Result;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("key")
public class KeyController {


    @PutMapping("/{id}/{oldkey}/{newkey}")
    public Result rename(String name) {
        //RedisTemplate
        return null;
    }

}
