package com.redis.manager.controller;

import com.alibaba.fastjson.JSON;
import com.redis.manager.entity.Result;
import com.redis.manager.model.RedisServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author agstar
 * @date 2020/7/11 15:05
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class ServerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @LocalServerPort
    private int port;
    RedisServer redisServer = new RedisServer();

    @BeforeEach
    void setUp() {

        redisServer.setHost("");
        redisServer.setName("");
        redisServer.setPort(6397);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addServer() throws Exception {
        this.mockMvc.perform(
                post("/server")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(redisServer)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JSON.toJSONString(Result.successMsg("添加成功"))));
    }

    @Test
    void testConnection() {
    }

    @Test
    void deleteServer() {
    }

    @Test
    void updateServer() {
    }

    @Test
    void getAllServer() {
    }
}