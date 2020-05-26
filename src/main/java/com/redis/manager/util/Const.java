package com.redis.manager.util;

import com.redis.manager.model.RedisServer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 常量配置类
 *
 * @author agstar
 */
public interface Const {

    Set<RedisServer> REDIS_SERVER = new LinkedHashSet<>();
    Map<String, ReactiveStringRedisTemplate> REDIS_TEMPLATE_MAP = new ConcurrentHashMap<>(16);
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
