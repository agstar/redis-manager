package com.redis.redismanage.util;

import com.redis.redismanage.model.RedisKey;
import com.redis.redismanage.model.RedisServer;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public interface Const {

    Set<RedisServer> REDIS_SERVER = new LinkedHashSet<>();
    Map<String, CopyOnWriteArrayList<RedisKey>> REDIS_KEYS_LISTMAP = new HashMap<>();
    /**
     * 文件统一编码
     */
    String CHARACTER = "UTF-8";
    /**
     * 默认数据库
     */
    int DATABASE_INDEX = 0;
    /**
     * redis 数据库个数
     */
    int REDIS_DEFAULT_DB_SIZE = 15;
    /**
     * 默认分隔符
     */
    String DEFAULT_SEPARATOR = "_";
}
