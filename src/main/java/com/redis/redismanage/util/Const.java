package com.redis.redismanage.util;

import com.redis.redismanage.model.RedisServer;

import java.util.LinkedHashSet;
import java.util.Set;

public interface Const {

    Set<RedisServer> REDIS_SERVER = new LinkedHashSet<>();
    /**
     * 文件统一编码
     */
    String CHARACTER = "UTF-8";

}
