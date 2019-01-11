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
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.redis.redismanage.util.Const.*;

@Component
@Log4j
public class RedisServerUtil {
    /**
     * 存储redis server的文件
     */
    private static String serverPath = "server/redisServer.json";
    private static Resource resource = new ClassPathResource(serverPath);
    @Autowired
    private IdWorker idWorker;

    /**
     * 将redisServer写入到文件中
     *
     * @author star
     */
    public synchronized void addServer(RedisServer redisServer) {
        try {
            redisServer.setId(idWorker.nextId());
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

    public synchronized void deleteServer(Long id) {
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

    public synchronized void updateServer(Long id, RedisServer redisServer) {
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

    public Set<RedisServer> getAllServer() {
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
     *
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
        for (int i = 0; i < Const.REDIS_DEFAULT_DB_SIZE; i++) {
            initRedisKeysCache(configuration, redisServer.getName(), i);
        }
    }

    private void initRedisKeysCache(RedisStandaloneConfiguration configuration, String serverName, int dbIndex) {
        RedisTemplate redisTemplate = new RedisTemplate();
        configuration.setDatabase(dbIndex);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        redisTemplate.setConnectionFactory(factory);
        List<RedisKey> redisKeyList = ConvertUtil.getRedisKeyList(redisTemplate);
        CopyOnWriteArrayList<RedisKey> redisKeys = new CopyOnWriteArrayList<>(redisKeyList);
        REDIS_KEYS_LISTMAP.put(serverName + DEFAULT_SEPARATOR + dbIndex, redisKeys);
    }


}
