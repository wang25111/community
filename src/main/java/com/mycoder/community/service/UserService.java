package com.mycoder.community.service;

import com.mycoder.community.dao.UserMapper;
import com.mycoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cj
 * @create 2021-12-09 18:08
 */
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    //根据id查询到用户
    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
