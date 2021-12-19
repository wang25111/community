package com.mycoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author cj
 * @create 2021-12-11 15:56
 */
public class CommunityUtil {

    //返回随机字符串（不包含"-"）
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //MD5加密（处理密码）
    public static String md5(String key){
        if(StringUtils.isBlank(key)) return null;
        //返回加密结果
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**返回json字符串*/
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();

        json.put("code", code);
        json.put("msg", msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }

        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

}
