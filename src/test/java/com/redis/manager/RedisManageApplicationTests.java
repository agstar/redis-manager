package com.redis.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class RedisManageApplicationTests {

    @Test
    public void contextLoads() {
        String key = "a:b:c:d";
        String[] keys = {"a:b:c:d", "a:b:c", "a:b:c:e", "a:b:d:d"};
        JSONArray json = new JSONArray();
        for (String s : keys) {
            json = getKeyTree2(s, json);
        }

        System.out.println(json.toString());
    }


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
