package com.redis.manager.handle;

import com.redis.manager.model.RedisKey;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author agstar
 */
@AllArgsConstructor
@Component
public class RedisContextHolder {
    private final HashValueHandler hashValueHandler;
    private final SetValueHandler setValueHandler;
    private final StringValueHandler stringValueHandler;
    private final ZsetValueHandler zsetValueHandler;
    private final ListValueHandler listValueHandler;
    private final TypeHandler typeHandler;
    Map<String, RedisValueHandler> container;

    @PostConstruct
    private void register() {
        container.put(DataType.HASH.code(),hashValueHandler);
        container.put(DataType.SET.code(),setValueHandler);
        container.put(DataType.STRING.code(),stringValueHandler);
        container.put(DataType.ZSET.code(),zsetValueHandler);
        container.put(DataType.LIST.code(),listValueHandler);
    }

    public RedisValueHandler getHandler(String type){
        return container.get(type);
    }

    public RedisValueHandler defaultHandler(String base64keyName, StringRedisTemplate stringRedisTemplate){
        RedisKey redisKey = typeHandler.getRedisKey(base64keyName, stringRedisTemplate);
        return container.get(redisKey.getType());
    }

}
