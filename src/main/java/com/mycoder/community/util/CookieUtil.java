package com.mycoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author cj
 * @create 2021-12-15 15:37
 */
public class CookieUtil {

    /**根据传入的request对象、cookie的名称（键），获取cookie的内容（value）*/
    public static String getValue(String name, HttpServletRequest request){
        if(request == null || name == null){
            throw new IllegalArgumentException("参数为空");
        }

        Cookie[] cookies = request.getCookies();

        for(Cookie cookie : cookies){
            if(cookie.getName().equals(name))
                return cookie.getValue();
        }

        return null;
    }
}
