package com.redis.manager;

import cn.hutool.core.util.RandomUtil;
import com.redis.manager.model.RedisServer;
import com.redis.manager.util.Const;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

/**
 * @author 25414
 * @date 2020/6/13 14:49
 */

public class InsertValueTest {

    private String hostname = "192.168.213.128";
    private int dbIndex = 0;
    private int port = 6379;
    private StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

    @Before
    public void setup() {
        //初始化redis连接
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(hostname);
        configuration.setPort(port);
        configuration.setDatabase(dbIndex);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();
        stringRedisTemplate.setConnectionFactory(factory);
        stringRedisTemplate.afterPropertiesSet();
        factory.getConnection().close();
    }

    @Test
    public void insertString() {
        for (int i = 0; i < 10; i++) {
            String key = "str" + i;
            stringRedisTemplate.opsForValue().set(key, key);
        }
    }

    @Test
    public void insertMap() {

        for (int i = 0; i < 10; i++) {
            Map<String, String> map = new HashMap<>(2000);
            for (int j = 0; j < 1005; j++) {
                map.put("hash" + j, "value" + j);
            }
            stringRedisTemplate.opsForHash().putAll("hashkey" + i, map);
        }

    }

    @Test
    public void insertList() {
        for (int i = 0; i < 10; i++) {
            List<String> objects = new ArrayList<>(2000);
            for (int j = 0; j < 2000; j++) {
                objects.add("listvalue" + j);
            }
            stringRedisTemplate.opsForList().leftPushAll("listkey" + i, objects);
        }
    }

    @Test
    public void insertSet() {
        for (int i = 0; i < 10; i++) {
            String[] arr = new String[2000];
            for (int j = 0; j < 2000; j++) {
                arr[j] = "setvalue" + j;
            }
            stringRedisTemplate.opsForSet().add("setkey" + i, arr);
        }
    }

    @Test
    public void insertZset() {
        for (int i = 0; i < 10; i++) {
            Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>(2000);
            for (int j = 0; j < 2000; j++) {
                String value = "setvalue" + j;
                double num = RandomUtil.randomDouble();
                ZSetOperations.TypedTuple<String> stringTypedTuple = new DefaultTypedTuple<>(value, num);
                set.add(stringTypedTuple);
            }
            stringRedisTemplate.opsForZSet().add("zsetkey" + i, set);
        }
    }


}
