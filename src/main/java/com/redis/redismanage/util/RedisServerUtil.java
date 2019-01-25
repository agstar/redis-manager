package com.redis.redismanage.util;

import com.alibaba.fastjson.JSON;
import com.redis.redismanage.model.RedisKey;
import com.redis.redismanage.model.RedisServer;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.redis.redismanage.util.Const.*;

@Component
@Log4j
public class RedisServerUtil {
    //    @Autowired
    private Pattern pattern = Pattern.compile("keys=(\\d*)");

    /**
     * 存储redis server的文件
     */
    private static String serverPath = "server/redisServer.json";
    private static Resource resource = new ClassPathResource(serverPath);

    /**
     * 将redisServer写入到文件中
     *
     * @author star
     */
    public synchronized void addServer(RedisServer redisServer) {
        try {
            REDIS_SERVER.add(redisServer);
            File file = resource.getFile();
            initRedisConnection(redisServer);
            String json = FileUtils.readFileToString(file, CHARACTER);
            if (StringUtils.isNotBlank(json)) {
                json = JSON.toJSONString(REDIS_SERVER);
                FileUtils.writeStringToFile(file, json, CHARACTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void deleteServer(Long id) {
        try {
            boolean remove = REDIS_SERVER.removeIf(x -> x.getId().equals(id));
            if (remove) {
                File file = resource.getFile();
                String json = FileUtils.readFileToString(file, CHARACTER);
                if (StringUtils.isNotBlank(json)) {
                    json = JSON.toJSONString(REDIS_SERVER);
                    FileUtils.writeStringToFile(file, json, CHARACTER);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void updateServer(Long id, RedisServer redisServer) {
        Optional<RedisServer> first = REDIS_SERVER.stream().filter(x -> x.getId().equals(id)).findFirst();
        if (first.isPresent()) {
            RedisServer oldRedisServer = first.get();
            oldRedisServer.setAuth(redisServer.getAuth());
            oldRedisServer.setHost(redisServer.getHost());
            oldRedisServer.setName(redisServer.getName());
            oldRedisServer.setPort(redisServer.getPort());
            //todo 修改
        }

    }

    public static Set<RedisServer> getAllServer() {
        return REDIS_SERVER;
    }

    /**
     * 读取文件中所有reids
     */
    public static void readAllServer() {
        File file;
        try {
            file = resource.getFile();
            String json = FileUtils.readFileToString(file, CHARACTER);
            if (StringUtils.isNotBlank(json)) {
                List<RedisServer> redisServers = JSON.parseArray(json, RedisServer.class);
                REDIS_SERVER.addAll(redisServers);
            }
        } catch (IOException e) {
            log.error("读取rediserver文件失败", e);
        }
    }

    /**
     * 初始化redis连接
     */
    public void initRedisConnection(RedisServer redisServer) {
        //初始化redis连接
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisServer.getHost());
        configuration.setDatabase(Const.DATABASE_INDEX);
        configuration.setPort(redisServer.getPort());
        if (StringUtils.isNotBlank(redisServer.getAuth())) {
            configuration.setPassword(redisServer.getAuth());
        }
        initRedisKeysCache(configuration, redisServer.getName());
    }

    private void initRedisKeysCache(RedisStandaloneConfiguration configuration, String serverName) {
        configuration.setDatabase(0);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(factory);
        stringRedisTemplate.afterPropertiesSet();
        List<RedisKey> redisKeyList;
        Map<String, Object> keycount = stringRedisTemplate.execute((RedisCallback<Map<String, Object>>) redisConnection -> {
            Properties info = redisConnection.info();
            //keys=37,expires=0,avg_ttl=0
            String keyspace = info.getProperty("db1");
            for (int i = 0; i < 16; i++) {
                String keys = info.getProperty("db" + i);
                int keyCount = 0;
                if (keys != null) {
                    Matcher matcher = pattern.matcher(keys);
                    String keyCountStr = matcher.group(1);
                    keyCount = Integer.parseInt(keyCountStr);
                }
                REDIS_KEYS_LISTMAP.put(serverName + DEFAULT_SEPARATOR + i, keyCount);
                REDIS_TEMPLATE_MAP.put(serverName + DEFAULT_SEPARATOR + i, stringRedisTemplate);
            }
            Map<String, Object> map = new HashMap<>();
            return map;
        });


    }


}
