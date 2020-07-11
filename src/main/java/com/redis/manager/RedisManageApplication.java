package com.redis.manager;

import com.redis.manager.util.RedisServerUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class RedisManageApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(RedisManageApplication.class, args);
        //读取所有redis
        RedisServerUtil.readAllServer();

    }
}
