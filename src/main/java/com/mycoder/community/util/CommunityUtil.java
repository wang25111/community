package com.mycoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

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

}
