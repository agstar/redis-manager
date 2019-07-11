package com.redis.manager.util;

import com.redis.manager.model.RedisKey;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertUtil {
    private Pattern pattern = Pattern.compile("keys=(\\d*)");

    @SuppressWarnings("unchecked")
    public static List<RedisKey> getRedisKeyList(StringRedisTemplate stringRedisTemplate) {
        Map<String, Object> keycount = stringRedisTemplate.execute((RedisCallback<Map<String, Object>>) redisConnection -> {
            Properties info = redisConnection.info();
            //keys=37,expires=0,avg_ttl=0
            String keyspace = info.getProperty("db1");
            Map<String, Object> map = new HashMap<>();
            return map;
        });
//        Object execute = stringRedisTemplate.execute(redisScript, new ArrayList<>(), "");
        Set<String> keys = stringRedisTemplate.keys("*");

        List<RedisKey> redisKeyList = new ArrayList<>();
        keys.forEach(x -> {
            RedisKey redisKey = RedisKey.builder().key(x).type(stringRedisTemplate.type(x)).build();
            redisKeyList.add(redisKey);
        });
        return redisKeyList;
    }


    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("keys=(\\d*)");
        String str="keys=37,expires=0,avg_ttl=0";
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            System.out.println(matcher.group());
            System.out.println(matcher.group(0));
            System.out.println(matcher.group(1));
        }
    }


}
