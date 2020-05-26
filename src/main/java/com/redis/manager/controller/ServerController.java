package com.redis.manager.controller;

import com.redis.manager.entity.Result;
import com.redis.manager.entity.StatusCode;
import com.redis.manager.util.Const;
import com.redis.manager.util.RedisServerUtil;
import org.apache.commons.lang3.StringUtils;
import com.redis.manager.model.RedisServer;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * server相关
 *
 * @author agstar
 */
@RestController
public class ServerController {
    private static final String PONG = "pong";

    /**
     * 添加一个redis服务
     *
     * @param redisServer 添加的reids服务信息
     */
    @PostMapping("server")
    public Result<Void> addServer(@RequestBody RedisServer redisServer) {
        RedisServerUtil.addServer(redisServer);
        return Result.successMsg("添加成功");
    }

    /**
     * ping
     *
     * @param redisServer
     * @return pong
     * @author agstar
     * @date 2020/5/26 21:09
     */
    @PostMapping("ping")
    public Mono<Result<String>> testConnection(@RequestBody RedisServer redisServer) {
        return RedisServerUtil.ping(redisServer).flatMap(pong -> {
            if (StringUtils.equalsIgnoreCase(pong, PONG)) {
                return Mono.just(Result.success(pong));
            }
            return Mono.just(Result.error());
        });
    }


    @DeleteMapping("server/{id}")
    public Result<Void> deleteServer(@PathVariable("id") Long id) {
        RedisServerUtil.deleteServer(id);
        return Result.successMsg("添加成功");
    }

    @PutMapping("server/{id}")
    public Result<Void> updateServer(@PathVariable() Long id, @RequestBody RedisServer redisServer) {
        RedisServerUtil.updateServer(id, redisServer);
        return Result.successMsg("修改成功");
    }

    /**
     * server list
     *
     * @return RedisServer
     * @author agstar
     * @date 2020/5/26 21:13
     */
    @GetMapping("server")
    public Mono<Result<Set<RedisServer>>> getAllServer() {
        Set<RedisServer> serverSet = new LinkedHashSet<>(Const.REDIS_SERVER);
        serverSet.forEach(x -> x.setAuth(""));
        return Mono.just(Result.success("查询成功", serverSet));
    }
}
