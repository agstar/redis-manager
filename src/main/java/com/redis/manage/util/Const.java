package com.redis.manage.util;

import com.redis.manage.model.RedisServer;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

public interface Const {

    Set<RedisServer> REDIS_SERVER = new LinkedHashSet<>();
    Map<String, Integer> REDIS_KEYS_LISTMAP = new HashMap<>();
    Map<String,List<Integer>> REDIS_KEY_COUNT = new HashMap<>();
    Map<String, StringRedisTemplate> REDIS_TEMPLATE_MAP = new HashMap<>();
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
