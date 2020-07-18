package com.redis.manager.controller;

import com.redis.manager.entity.Result;
import com.redis.manager.util.Const;
import com.redis.manager.util.RedisServerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import com.redis.manager.model.RedisServer;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * @author agstar
 */
@Api(tags="Server管理")
@RestController
public class ServerController {


    /**
     * 添加一个redis服务
     *
     * @param redisServer 添加的reids服务信息
     */
    @ApiOperation("添加server")
    @PostMapping("server")
    public Result<Void> addServer(@ApiParam("server信息")@RequestBody RedisServer redisServer) {
        RedisServerUtil.addServer(redisServer);
        return Result.successMsg("添加成功");
    }

    @PostMapping("ping")
    public Result<String> testConnection(@RequestBody RedisServer redisServer) {
        String pong = RedisServerUtil.ping(redisServer);
        if (StringUtils.equalsIgnoreCase(pong, "PONG")) {
            return Result.success(pong, pong);
        }
        return Result.errorMsg(pong);

    }


    @DeleteMapping("server/{serverName}")
    public Result<Void> deleteServer(@PathVariable("serverName") String serverName) {
        RedisServerUtil.deleteServer(serverName);
        return Result.successMsg("添加成功");
    }

    @PutMapping("server/{serverName}")
    public Result<Void> updateServer(@PathVariable("serverName") String serverName, @RequestBody RedisServer redisServer) {
        RedisServerUtil.updateServer(serverName, redisServer);
        return Result.successMsg("修改成功");
    }

    @GetMapping("server")
    public Result<Set<RedisServer>> getAllServer() {
        Set<RedisServer> serverSet = new LinkedHashSet<>(Const.REDIS_SERVER);
        serverSet.forEach(x -> x.setAuth(""));
        return Result.success("查询成功", serverSet);
    }
}
