package com.redis.manager.model;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author 25414
 */
@Data
@Builder
public class RedisKey {
    @NotBlank
    private String serverName;
    private String keyName;
    private String type;
    /**
     * 使用base64编码的keyname，避免部分byte类型的keyname转为string乱码，无法查询数据
     * 获取数据时都使用此keyname
     */
    private String base64KeyName;
    /**hash 结构中的key*/
    private String hashKey;
    @NotBlank
    private int dbIndex;
    private Object keyValue;
    private Double score;
    private long ttl;


}
