package com.redis.manager.controller;

import com.alibaba.fastjson.JSONArray;
import com.redis.manager.entity.Result;
import com.redis.manager.handler.KeyHandler;
import com.redis.manager.handler.RedisContextHolder;
import com.redis.manager.model.RedisKey;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author agstar
 */
@RestController
@AllArgsConstructor
public class KeyValueController {
    private final RedisContextHolder redisContextHolder;
    private final KeyHandler keyHandler;

    /**
     * 获取dbindex中的所有key
     *
     * @author agstar
     * @date 2019/6/13 21:13
     */
    @GetMapping("key")
    public Result<JSONArray> getKeyList(@Valid RedisKey redisKey) {
        return Result.success(keyHandler.keys(redisKey));
    }


    /**
     * 获取值
     */
    @GetMapping("value")
    public Result<Object> getValue(@Valid RedisKey redisKey) {
        Object value = redisContextHolder.getHandler(redisKey.getType()).getValue(redisKey);
        return Result.success(value);
    }

    /**
     * 获取16个数据库中的key数量
     *
     * @author agstar
     * @date 2019/6/13 22:37
     */
    @ApiOperation(value = "获取16个数据库中的key数量")
    @GetMapping("keyCount/{serverName}")
    public Result<List<Integer>> getKeyCount(@PathVariable("serverName") String serverName) {
        return Result.success(keyHandler.keyCount(serverName));
    }

    /**
     * 添加key
     */
    @PostMapping("key/{serverName}/{dbIndex}")
    public Result<Void> addKey(@RequestBody RedisKey redisKey) {
        redisContextHolder.getHandler(redisKey.getType()).saveKey(redisKey);
        return Result.success();
    }

    /**
     * 删除key
     */
    @PostMapping("key/delete/{serverName}/{dbIndex}")
    public Result<Void> deleteKey(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @RequestBody List<String> keyList) {
        keyHandler.deleteKey(serverName, dbIndex, keyList);
        return Result.success();
    }

    /**
     * 重命名key
     */
    @PutMapping("key/{serverName}/{dbIndex}/{oldKeyName}/{newKeyName}")
    public Result<Void> rename(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @PathVariable("oldKeyName") String oldKeyName, @PathVariable("newKeyName") String newKeyName) {
        keyHandler.renameKey(null, newKeyName);
        return Result.success();
    }

    /**
     * update ttl
     */
    @PutMapping("key/ttl/{serverName}/{dbIndex}/{keyName}/{ttl}")
    public Result<Void> updateTtl(@PathVariable("serverName") String serverName, @PathVariable("dbIndex") int dbIndex, @PathVariable("keyName") String keyName, @PathVariable("ttl") Long ttl) {
        if (keyHandler.updateTtl(serverName, dbIndex, keyName, ttl)) {
            return Result.success();
        } else {
            return Result.errorMsg("设置ttl失败");
        }

    }


}
