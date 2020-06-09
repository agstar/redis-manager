package com.redis.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class RedisManageApplicationTests {

    @Test
    public void contextLoads() {
        String key = "a:b:c:d";
        String[] keys = {"a", "a:b", "h:g", "a:b:c:d", "a:b:c", "a:b:c:e", "a:b:d:d"};
        JSONArray json = new JSONArray();
        for (String s : keys) {
            System.out.println(s);
            json = getKeyTree3(s, json);
        }
        System.out.println(json.toJSONString());
        JSONArray data = new JSONArray();
        convertTree(json, null, data);
        System.out.println(data.toJSONString());
//        HashMap<String, String> map = new HashMap<>();
//        map.put("", "");
//        map.get("");
//        StringBuffer sb = new StringBuffer();
//        sb.append(1);
//        System.out.println(json.toString());

    }

    private void convertTree(JSONArray json, String parent, JSONArray children) {
        List<Object> parentList = json.stream().filter(x -> {
            JSONObject obj = (JSONObject) x;
            return StringUtils.equals(obj.getString("parent"), parent);
        }).collect(Collectors.toList());
        parentList.forEach(x -> {
            JSONObject obj = (JSONObject) x;
            Boolean isLast = obj.getBoolean("isLast");
            JSONObject jsonObject = new JSONObject();
            String label = obj.getString("label");
            if (isLast) {
                jsonObject.put("label", label);
                children.add(jsonObject);
            } else {
                jsonObject.put("label", label);
                JSONArray tempChildren = new JSONArray();
                jsonObject.put("children", tempChildren);
                children.add(jsonObject);
                convertTree(json, label, tempChildren);
            }
        });
    }


    public JSONArray getKeyTree3(String keyName, JSONArray dataArray) {
        String[] keyArray = keyName.split(":");
        if (keyArray.length == 1) {
            JSONObject newObj = new JSONObject();
            newObj.put("label", keyName);
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
                    JSONObject tempJSONObj = new JSONObject();
                    tempJSONObj.put("label", keyName);
                    tempJSONObj.put("isLast", true);
                    tempJSONObj.put("parent", lastKeyName);
                    dataArray.add(tempJSONObj);
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
                    tempJSONObj.put("isLast", false);
                    dataArray.add(tempJSONObj);
                }
                lastKeyName = tempKeyName;
            }
        }

        return dataArray;
    }


    public JSONArray getKeyTree2(String key, JSONArray dataArray) {
        JSONArray temp = dataArray;
        String[] keyArray = key.split(":");
        JSONObject jsonObj = new JSONObject();
        boolean hasRepeate = false;
        for (Object o : dataArray) {
            JSONObject json = (JSONObject) o;
            if (StringUtils.equals(json.getString("label"), (keyArray[0]))) {
                hasRepeate = true;
                jsonObj = json;
                break;
            }
        }
        if (!hasRepeate) {
            jsonObj.put("label", keyArray[0]);
            dataArray.add(jsonObj);
        }
        if (keyArray.length != 1) {
            JSONArray childrens = (JSONArray) jsonObj.getOrDefault("children", new JSONArray());
            jsonObj.put("children", childrens);
            int lastIndex = keyArray.length - 1;
            for (int i = 1; i < keyArray.length; i++) {
                String tempKeyName = keyArray[i];
                //不是最后一个
                if (i != lastIndex) {
                    boolean hasChildRepeate = false;
                    for (int j = 0; j < childrens.size(); j++) {
                        JSONObject json = childrens.getJSONObject(j);
                        if (json.get("label").equals(tempKeyName)) {
                            hasChildRepeate = true;
                            jsonObj = json;
                            break;
                        }
                    }
                    //如果没有循环找下去没有重复的
                    if (!hasChildRepeate) {
                        jsonObj = new JSONObject();
                        jsonObj.put("label", tempKeyName);
                        childrens.add(jsonObj);
                        jsonObj.put("children", new JSONArray());
                    }
                    childrens = (JSONArray) jsonObj.getOrDefault("children", new JSONArray());
                } else {
                    JSONObject tempObj = new JSONObject();
                    BeanUtils.copyProperties(jsonObj, tempObj);
                    temp.add(tempObj);
                    jsonObj = new JSONObject();
                    jsonObj.put("label", key);
                    childrens.add(jsonObj);
                    dataArray = temp;
                }
            }
        }
        return dataArray;
    }


    public JSONObject getKeyTree(String key) {
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

}
