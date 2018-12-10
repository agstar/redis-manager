package com.redis.redismanage.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.Feature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.redis.redismanage.model.RedisServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.redis.redismanage.util.Const.CHARACTER;
import static com.redis.redismanage.util.Const.REDIS_SERVER;

@Component
public class RedisServerUtil {
    /**
     * 存储redis server的文件
     */
    private static String serverPath = "server/redisServer.json";
    @Autowired
    private IdWorker idWorker;

    /**
     * 将redisServer写入到文件中
     *
     * @author star
     */
    public synchronized void addServer(RedisServer redisServer) {
        Resource resource = new ClassPathResource(serverPath);
        try {
            redisServer.setId(idWorker.nextId());
            REDIS_SERVER.add(redisServer);
            File file = resource.getFile();
            String json = FileUtils.readFileToString(file, CHARACTER);
            if (StringUtils.isNotBlank(json)) {
                json = JSON.toJSONString(REDIS_SERVER);
                FileUtils.writeStringToFile(file, json, CHARACTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteServer(RedisServer redisServer) {
        Resource resource = new ClassPathResource(serverPath);
        try {
            REDIS_SERVER.remove(redisServer);
            File file = resource.getFile();
            String json = FileUtils.readFileToString(file, CHARACTER);
            if (StringUtils.isNotBlank(json)) {
                json = JSON.toJSONString(REDIS_SERVER);
                FileUtils.writeStringToFile(file, json, CHARACTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
