package com.redis.redismanage.controller;

import com.redis.redismanage.entity.Result;
import com.redis.redismanage.entity.StatusCode;
import com.redis.redismanage.model.RedisServer;
import com.redis.redismanage.util.RedisServerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.swing.StringUIClientPropertyKey;

import static com.redis.redismanage.util.Const.REDIS_SERVER;

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
        return new Result(true, StatusCode.OK, "添加成功");
    }

    public Result testConnection(@RequestBody RedisServer redisServer) {
        String ping = RedisServerUtil.ping(redisServer);
        if (StringUtils.equalsIgnoreCase(ping, "pong")) {
            return new Result(true, StatusCode.OK, ping);
        }
        return new Result(false, StatusCode.ERROR, ping);

    }


    @DeleteMapping("server/{id}")
    public Result deleteServer(@PathVariable("id") Long id) {
        RedisServerUtil.deleteServer(id);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @PutMapping("server/{id}")
    public Result updateServer(@PathVariable() Long id, @RequestBody RedisServer redisServer) {
        RedisServerUtil.updateServer(id, redisServer);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    @GetMapping("server")
    public Result getAllServer() {
        return new Result(true, StatusCode.OK, "查询成功", REDIS_SERVER);
    }

}
