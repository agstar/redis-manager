package com.redis.redismanage.controller;

import com.redis.redismanage.entity.Result;
import com.redis.redismanage.entity.StatusCode;
import com.redis.redismanage.model.RedisServer;
import com.redis.redismanage.util.RedisServerUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("redis")
public class RedisController {

    @PostMapping("/")
    public Result addServer(@RequestBody RedisServer redisServer) {
        RedisServerUtil.addServer(redisServer);
        return new Result(true, StatusCode.OK, "查询成功");
    }


}
