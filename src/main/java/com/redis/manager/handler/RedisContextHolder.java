package com.redis.manager.handler;

import com.redis.manager.model.RedisKey;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    private final RedisValueHandler defaultValueHandler = new DefaultValueHandler();
    Map<String, RedisValueHandler> container;

    @PostConstruct
    private void register() {
        container.put(DataType.HASH.code(), hashValueHandler);
        container.put(DataType.SET.code(), setValueHandler);
        container.put(DataType.STRING.code(), stringValueHandler);
        container.put(DataType.ZSET.code(), zsetValueHandler);
        container.put(DataType.LIST.code(), listValueHandler);
    }

    /**
     * 获取对应类型的处理器
     * @param type redis中五种数据类型 {@link DataType#code()}
     * @return RedisValueHandler
     * @author agstar
     * @date 2020/7/11 14:33
     */
    public RedisValueHandler getHandler(String type) {
        RedisValueHandler redisValueHandler = container.get(type);
        if (redisValueHandler == null) {
            return defaultValueHandler;
        }
        return redisValueHandler;
    }


}
