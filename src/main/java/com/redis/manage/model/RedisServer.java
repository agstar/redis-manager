package com.redis.manage.model;

import lombok.Data;

/**
 * reids服务
 *
 * @author agstar
 */
@Data
public class RedisServer {
    private Long id;
    private String name;
    private String host;
    private int port;
    private String auth;

}
