package com.redis.manager.handle;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.DataType;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
//@Configuration
public class RedisConfiguration {
    private final HashValueHandler hashValueHandler;
    private final SetValueHandler setValueHandler;
    private final StringValueHandler stringValueHandler;
    private final ZsetValueHandler zsetValueHandler;
    private final ListValueHandler listValueHandler;
    Map<String, RedisValueHandler> container = new HashMap<>();

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

}
