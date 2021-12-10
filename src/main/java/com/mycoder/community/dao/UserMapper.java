package com.mycoder.community.dao;

import com.mycoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author cj
 * @create 2021-12-08 20:53
 */
//映射文件在包mapper中
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String userName);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus( int id,  int status);
    //更改头像
    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
