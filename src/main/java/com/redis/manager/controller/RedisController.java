package com.redis.manager.controller;

import com.redis.manager.entity.Result;
import com.redis.manager.entity.StatusCode;
import com.redis.manager.util.Const;
import com.redis.manager.util.RedisServerUtil;
import org.apache.commons.lang3.StringUtils;
import com.redis.manager.model.RedisServer;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.Set;


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

    @PostMapping("ping")
    public Result testConnection(@RequestBody RedisServer redisServer) {
        String pong = RedisServerUtil.ping(redisServer);
        if (StringUtils.equalsIgnoreCase(pong, "pong")) {
            return new Result(true, StatusCode.OK, pong);
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
        Set<RedisServer> serverSet = new LinkedHashSet<>(Const.REDIS_SERVER);
        serverSet.forEach(x -> x.setAuth(""));
        return Result.success("查询成功", serverSet);
    }
}
