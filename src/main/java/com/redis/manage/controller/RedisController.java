package com.redis.manage.controller;

import com.redis.manage.entity.Result;
import com.redis.manage.model.RedisServer;
import com.redis.manage.util.RedisServerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.redis.manage.util.Const.REDIS_SERVER;

@RestController
public class RedisController {


    /**
     * 添加一个redis服务
     *
     * @param redisServer 添加的reids服务信息
     */
    @PostMapping("server")
    public Result addServer(@RequestBody RedisServer redisServer) {
        RedisServerUtil.addServer(redisServer);
        return Result.successMsg("添加成功");
    }

    @PostMapping("testConnection")
    public Result testConnection(@RequestBody RedisServer redisServer) {
        String ping = RedisServerUtil.ping(redisServer);
        if (StringUtils.equalsIgnoreCase(ping, "pong")) {
            return Result.success(ping);
        }
        return Result.error();

    }


    @DeleteMapping("server/{id}")
    public Result deleteServer(@PathVariable("id") Long id) {
        RedisServerUtil.deleteServer(id);
        return Result.successMsg("添加成功");
    }

    @PutMapping("server/{id}")
    public Result updateServer(@PathVariable() Long id, @RequestBody RedisServer redisServer) {
        RedisServerUtil.updateServer(id, redisServer);
        return Result.successMsg("修改成功");
    }

    @GetMapping("server")
    public Result getAllServer() {
        Set<RedisServer> serverSet = new LinkedHashSet<>(REDIS_SERVER);
        serverSet.forEach(x -> x.setAuth(""));
        return Result.success("查询成功", serverSet);
    }
}
