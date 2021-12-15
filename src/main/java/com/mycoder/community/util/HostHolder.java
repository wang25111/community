package com.mycoder.community.util;

import com.mycoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author cj
 * @create 2021-12-15 19:03
    持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User>  threadLocal = new ThreadLocal<>();
    //存入用户
    public void setUser(User user){
        threadLocal.set(user);
    }

    //取出用户
    public User getUser(){
        return threadLocal.get();
    }

    //清理用户
    public void clear(){
        threadLocal.remove();
    }
}
