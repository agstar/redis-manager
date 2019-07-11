package com.redis.manager.model;


import lombok.*;
import org.springframework.data.redis.connection.DataType;

@Data
@Builder
public class RedisKey {
    private String key;

    private DataType type;
    private String keyName;
    private int dbIndex;
    private String keyValue;



}
