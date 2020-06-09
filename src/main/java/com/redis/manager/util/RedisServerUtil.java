package com.redis.manager.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redis.manager.model.RedisKey;
import com.redis.manager.model.RedisServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.redis.manager.util.Const.*;

/**
 * @author agstar
 */
@Component
@Slf4j
public class RedisServerUtil {
    private static final Pattern PATTERN = Pattern.compile("keys=(\\d*)");
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    /**
     * 存储redis server的文件
     */
    private static String serverPath = PROJECT_PATH + "/server.json";
    private static Resource resource = new ClassPathResource(serverPath);

    /**
     * 将redisServer写入到文件中
     *
     * @author star
     */
    public static synchronized void addServer(RedisServer redisServer) {
        try {
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
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(json)) {
                List<RedisServer> redisServers = JSON.parseArray(json, RedisServer.class);
                REDIS_SERVER.addAll(redisServers);
            }
        } catch (IOException e) {
            log.error("读取rediserver文件失败", e);
        }
    }

    /**
     * 初始化加载所有key数量
     *
     * @author rcl
     * @date 2019/1/28 9:40
     */
    public static void initKeyCount() {
        for (RedisServer redisServer : REDIS_SERVER) {
            List<Integer> count = getRedisKeyCount(redisServer);
            REDIS_KEY_COUNT.put(redisServer.getName(), count);
        }
    }

    public static List<Integer> getRedisKeyCount(RedisServer redisServer) {
        StringRedisTemplate stringRedisTemplate = RedisServerUtil.initRedisConnection(redisServer, 0);
        return initRedisKeysCache(stringRedisTemplate);
    }

    /**
     * 初始化redis连接
     */
    public static StringRedisTemplate initRedisConnection(RedisServer redisServer, int dbIndex) {
        StringRedisTemplate stringRedisTemplate = null;
        try {
            //初始化redis连接
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
            configuration.setHostName(redisServer.getHost());
            configuration.setDatabase(Const.DATABASE_INDEX);
            configuration.setPort(redisServer.getPort());
            if (StringUtils.isNotBlank(redisServer.getAuth())) {
                configuration.setPassword(redisServer.getAuth());
            }
            configuration.setDatabase(dbIndex);
            LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
            factory.afterPropertiesSet();
            stringRedisTemplate = new StringRedisTemplate();
            stringRedisTemplate.setConnectionFactory(factory);
            stringRedisTemplate.afterPropertiesSet();
            factory.getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return stringRedisTemplate;
    }

    private static List<Integer> initRedisKeysCache(StringRedisTemplate stringRedisTemplate) {
        return stringRedisTemplate.execute((RedisCallback<List<Integer>>) redisConnection -> {
            Properties info = redisConnection.info();
            //keys=37,expires=0,avg_ttl=0
            // String keyspace = info.getProperty("db1");
            List<Integer> keyCountList = new ArrayList<>(16);
            for (int i = 0; i < 16; i++) {
                assert info != null;
                String keys = info.getProperty("db" + i);
                int keyCount = 0;
                if (keys != null) {
                    Matcher matcher = PATTERN.matcher(keys);
                    if (matcher.find()) {
                        String keyCountStr = matcher.group(1);
                        keyCount = Integer.parseInt(keyCountStr);
                    }

                }
                keyCountList.add(keyCount);
            }
            return keyCountList;
        });
    }

    public static String ping(RedisServer redisServer) {
        //初始化redis连接
        try {
            StringRedisTemplate stringRedisTemplate = initRedisConnection(redisServer, 0);
            return stringRedisTemplate.execute(RedisConnectionCommands::ping);
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    private static void convertTree(JSONArray json, String parent, JSONArray children) {
        List<Object> parentList = json.stream().filter(x -> {
            JSONObject obj = (JSONObject) x;
            return StringUtils.equals(obj.getString("parent"), parent);
        }).collect(Collectors.toList());
        parentList.forEach(x -> {
            JSONObject obj = (JSONObject) x;
            Boolean isLast = obj.getBoolean("isLast");
            JSONObject jsonObject = new JSONObject();
            String label = obj.getString("label");
            String index = obj.getString("index");
            String type = obj.getString("type");
            String base64KeyName = obj.getString("base64KeyName");
            if (isLast) {
                jsonObject.put("label", label);
                jsonObject.put("index", index);
                jsonObject.put("type", type);
                jsonObject.put("base64KeyName", base64KeyName);
                children.add(jsonObject);
            } else {
                jsonObject.put("label", label);
                jsonObject.put("index", index);
                jsonObject.put("type", type);
                jsonObject.put("base64KeyName", base64KeyName);
                JSONArray tempChildren = new JSONArray();
                jsonObject.put("children", tempChildren);
                children.add(jsonObject);
                convertTree(json, label, tempChildren);
            }
        });
    }


    private static JSONArray getKeyTree3(RedisKey redisKey, JSONArray dataArray, String index) {
        String keyName = redisKey.getKeyName();
        String base64KeyName = redisKey.getBase64KeyName();
        String type = redisKey.getType();
        String[] keyArray = keyName.split(":");
        if (keyArray.length == 1) {
            JSONObject newObj = new JSONObject();
            newObj.put("label", keyName);
            newObj.put("index", index);
            newObj.put("base64KeyName", base64KeyName);
            newObj.put("type", type);
            newObj.put("isLast", true);
            dataArray.add(newObj);
        } else {
            String lastKeyName = null;
            for (int i = 0; i < keyArray.length; i++) {
                String tempKeyName = keyArray[i];
                List<Object> objectList = dataArray.stream().filter(o -> {
                    JSONObject obj = (JSONObject) o;
                    String label = obj.getString("label");
                    return label.equals(tempKeyName);
                }).collect(Collectors.toList());
                //最后一个
                if (i == keyArray.length - 1) {
                    JSONObject tempjsonobj = new JSONObject();
                    tempjsonobj.put("label", keyName);
                    tempjsonobj.put("index", index);
                    tempjsonobj.put("base64KeyName", base64KeyName);
                    tempjsonobj.put("isLast", true);
                    tempjsonobj.put("type", type);
                    tempjsonobj.put("parent", lastKeyName);
                    dataArray.add(tempjsonobj);
                    break;
                }
                if (!CollectionUtils.isEmpty(objectList)) {
                    if (objectList.size() < 2) {
                        for (Object o : objectList) {
                            JSONObject obj = (JSONObject) o;
                            Boolean isLast = obj.getBoolean("isLast");
                            //不是最后一个
                            if (isLast) {
                                JSONObject tempJSONObj = new JSONObject();
                                tempJSONObj.put("label", obj.getString("label"));
                                tempJSONObj.put("parent", lastKeyName);
                                tempJSONObj.put("index", index);
                                tempJSONObj.put("type", type);
                                tempJSONObj.put("base64KeyName", base64KeyName);
                                tempJSONObj.put("isLast", false);
                                dataArray.add(tempJSONObj);
                                break;
                            }
                        }
                    }
                } else {
                    JSONObject tempJSONObj = new JSONObject();
                    tempJSONObj.put("label", tempKeyName);
                    tempJSONObj.put("parent", lastKeyName);
                    tempJSONObj.put("index", index);
                    tempJSONObj.put("base64KeyName", base64KeyName);
                    tempJSONObj.put("isLast", false);
                    dataArray.add(tempJSONObj);
                }
                lastKeyName = tempKeyName;
            }
        }

        return dataArray;
    }

    /**
     * 递归获取树形菜单
     */
    public static JSONArray getKeyTree(Set<RedisKey> keys, String serverName, int dbIndex) {
        JSONArray data = new JSONArray();
        if (keys != null) {
            String index = serverName + ":" + dbIndex;
            JSONArray dataArray = new JSONArray();
            for (RedisKey redisKey : keys) {
                getKeyTree3(redisKey, dataArray, index);
            }
            convertTree(dataArray, null, data);
        }
        return data;
    }

    public static void getKeyTree2(RedisKey redisKey, JSONArray jsonArray, String serverName, int dbIndex) {
        String index = serverName + ":" + dbIndex;
        //JSONArray temp = jsonArray;
        String key = redisKey.getKeyName();
        String[] array = key.split(":");
        JSONObject jsonObj = new JSONObject();
        Optional<Object> label = jsonArray.stream().filter(x -> ((JSONObject) x).get("label").equals(array[0])).findFirst();
        if (label.isPresent()) {
            jsonObj = (JSONObject) label.get();
        } else {
            //没有重复的
            jsonObj.put("label", array[0]);
            jsonObj.put("index", index);
            jsonObj.put("type", redisKey.getType());
            jsonArray.add(jsonObj);
        }
        if (array.length != 1) {
            JSONArray childrens = (JSONArray) jsonObj.getOrDefault("children", new JSONArray());
            jsonObj.put("children", childrens);
            int lastIndex = array.length - 1;
            for (int i = 1; i < array.length; i++) {
                String s = array[i];
                //不是最后一个
                if (i != lastIndex) {
                    boolean hasChildRepeate = false;
                    for (Object o : childrens) {
                        JSONObject json = (JSONObject) o;
                        if (json.get("label").equals(s)) {
                            hasChildRepeate = true;
                            jsonObj = json;
                            break;
                        }
                    }
                    if (!hasChildRepeate) {
                        jsonObj = new JSONObject();
                        jsonObj.put("label", s);
                        jsonObj.put("type", redisKey.getType());
                        jsonObj.put("index", index);
                        childrens.add(jsonObj);
                        jsonObj.put("children", new JSONArray());
                        childrens = jsonObj.getJSONArray("children");
                    } else {
                        childrens = jsonObj.getJSONArray("children");
                    }
                } else {
                    jsonObj = new JSONObject();
                    jsonObj.put("label", key);
                    jsonObj.put("index", index);
                    jsonObj.put("type", redisKey.getType());
                    childrens.add(jsonObj);
                    //jsonArray = temp;
                }
            }
        }
    }

    public static JSONObject getKeyTree(String key) {
        JSONObject json = new JSONObject();
        String[] array = key.split(":");
        json.put("label", array[0]);
        if (array.length != 1) {
            JSONArray childrens = (JSONArray) json.getOrDefault("children", new JSONArray());
            json.put("children", childrens);
            int lastIndex = array.length - 1;
            for (int i = 1; i < array.length; i++) {
                String s = array[i];
                JSONObject temp = new JSONObject();
                if (i != lastIndex) {
                    temp.put("label", s);
                    childrens.add(temp);
                    JSONArray tempArray = new JSONArray();
                    temp.put("children", tempArray);
                    childrens = (JSONArray) temp.get("children");
                } else {
                    temp.put("label", key);
                    childrens.add(temp);
                }
            }
        }
        return json;
    }

    //(RedisKey redisKey, JSONArray jsonArray, String serverName, int dbIndex)


    public JSONArray getKeyTree2(String key, JSONArray jsonArray) {
        JSONArray temp = jsonArray;
        String[] array = key.split(":");
        JSONObject jsonObj = new JSONObject();
        boolean hasRepeate = false;
        for (Object o : jsonArray) {
            JSONObject json = (JSONObject) o;
            if (json.get("label").equals(array[0])) {
                hasRepeate = true;
                jsonObj = json;
                break;
            }
        }
        if (!hasRepeate) {
            jsonObj.put("label", array[0]);
            jsonArray.add(jsonObj);
        }
        if (array.length != 1) {
            JSONArray childrens = (JSONArray) jsonObj.getOrDefault("children", new JSONArray());
            jsonObj.put("children", childrens);
            int lastIndex = array.length - 1;
            for (int i = 1; i < array.length; i++) {
                String s = array[i];
                //不是最后一个
                if (i != lastIndex) {
                    boolean hasChildRepeate = false;
                    for (Object o : childrens) {
                        JSONObject json = (JSONObject) o;
                        if (json.get("label").equals(s)) {
                            hasChildRepeate = true;
                            jsonObj = json;
                            break;
                        }
                    }
                    if (!hasChildRepeate) {
                        jsonObj = new JSONObject();
                        jsonObj.put("label", s);
                        childrens.add(jsonObj);
                        jsonObj.put("children", new JSONArray());
                        childrens = jsonObj.getJSONArray("children");
                    } else {
                        childrens = jsonObj.getJSONArray("children");
                    }
                } else {
                    jsonObj = new JSONObject();
                    jsonObj.put("label", key);
                    childrens.add(jsonObj);
                    jsonArray = temp;
                }
            }
        }
        return jsonArray;
    }


}
