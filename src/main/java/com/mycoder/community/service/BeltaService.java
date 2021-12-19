package com.mycoder.community.service;

import com.mycoder.community.dao.UserMapper;
import com.mycoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author cj
 * @create 2021-12-19 14:57
 */
@Component
public class BeltaService {


    @Autowired
    UserMapper userMapper;

//    @Transactional(propagation = Propagation.REQUIRED)
    public void insertB(){
        User user = new User();
        user.setUserName("方法B1");
        user.setSalt("sdf7");
        user.setCreateTime(new Date());
        user.setPassword("123456");
        userMapper.insertUser(user);

        int a = 1 / 0;

        user = new User();
        user.setUserName("方法2");
        user.setSalt("sdf7");
        user.setCreateTime(new Date());
        user.setPassword("123456");
        userMapper.insertUser(user);
    }
}
