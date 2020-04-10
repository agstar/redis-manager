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

        //RedisServerUtil.initKeyCount();
//        String filePath = System.getProperty("user.dir");
//        System.out.println("filePath: " + filePath);
//        /*  此方法，传入参数为String，不能带/  */
//
//        String path = "server/server1.json";
//        InputStream resourceAsStream = RedisManageApplication.class.getClassLoader().getResourceAsStream(path);
//        String s = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
//
//        System.out.println(s);
//        /*  此方法，传入参数为String，不能带/  */
//        resourceAsStream = RedisManageApplication.class.getClass().getResourceAsStream("/templates/server/server.json");
    }
}
