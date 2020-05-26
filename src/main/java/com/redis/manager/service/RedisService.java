package com.redis.manager.service;

import com.redis.manager.entity.Result;
import com.redis.manager.handler.RedisContextHolder;
import com.redis.manager.model.RedisKey;
import com.redis.manager.model.RedisServer;
import com.redis.manager.util.RedisServerUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.redis.manager.util.Const.*;

/**
 * @author agstar
 */
@AllArgsConstructor
@Service
public class RedisService {
    private static final ScanOptions SCAN_OPTIONS = new ScanOptions.ScanOptionsBuilder().match("*").count(10000).build();
    private final RedisContextHolder redisContextHolder;


    /**
     *
     * @param redisKey key的信息
     * @return
     * @author agstar
     * @date 2020/5/26 20:20
     */
    public Object getValue(RedisKey redisKey) {
//        String type = redisKey.getType();
//        String serverName = redisKey.getServerName();
//        int dbIndex = redisKey.getDbIndex();
//        String base64keyName = redisKey.getBase64KeyName();
//        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate(serverName, dbIndex);
        Object value = null;//redisContextHolder.getHandler(type).getValue(base64keyName, stringRedisTemplate);
        return Result.success(value);

    }



}
