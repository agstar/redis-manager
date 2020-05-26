package com.redis.manager.handler;

import com.redis.manager.model.RedisKey;
import com.redis.manager.model.RedisServer;
import com.redis.manager.util.RedisServerUtil;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Base64;
import java.util.Optional;

import static com.redis.manager.util.Const.*;

/**
 * value处理器
 *
 * @author agstar
 * @date 2020/4/23 9:35
 */
public abstract class RedisValueHandler {


     protected ReactiveStringRedisTemplate getStringRedisTemplate(String serverName, int dbIndex) {
         ReactiveStringRedisTemplate reactiveStringRedisTemplate = REDIS_TEMPLATE_MAP.get(serverName + DEFAULT_SEPARATOR + dbIndex);
        if (reactiveStringRedisTemplate == null) {
            Optional<RedisServer> first = REDIS_SERVER.stream().filter(x -> x.getName().equals(serverName)).findFirst();
            if (first.isPresent()) {
                return RedisServerUtil.initRedisConnection(first.get(), dbIndex);
            }
        }
        throw new RuntimeException("初始化StringRedisTemplate失败");
    }

    /**
     * 
     *
     * @author agstar
     * @date 2020/4/23 9:25
     */
    public abstract Object getValue(RedisKey redisKey);

}
