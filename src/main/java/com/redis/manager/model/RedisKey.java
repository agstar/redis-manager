package com.redis.manager.model;


import lombok.*;
import org.springframework.data.redis.connection.DataType;

@Data
@Builder
public class RedisKey {
    private String keyName;
    private String type;
    private String hashKey;
    private int dbIndex;
    private Object keyValue;
    private Double score;
    private long ttl;


}
