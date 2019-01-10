package com.redis.redismanage.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.connection.DataType;
@Getter
@Setter
public class RedisKey {
    private String key;
    private DataType type;
}
