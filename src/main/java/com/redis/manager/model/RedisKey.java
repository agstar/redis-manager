package com.redis.manager.model;


import lombok.*;
import org.springframework.data.redis.connection.DataType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisKey {
    private String key;
    private DataType type;
}
