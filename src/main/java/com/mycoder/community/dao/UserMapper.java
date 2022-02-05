package com.mycoder.community.dao;

import com.mycoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author cj
 * @create 2021-12-08 20:53
 */
//映射文件在包mapper中
@Mapper
public interface UserMapper {

    User selectById(@Param("id") int id);

    User selectByName(@Param("userName") String userName);

    User selectByEmail(@Param("email") String email);

    int insertUser(User user);

    int updateStatus( @Param("id") int id,  @Param("status") int status);
    //更改头像
    int updateHeader(@Param("id") int id, @Param("headerUrl") String headerUrl);

    int updatePassword(@Param("id") int id, @Param("password") String password);
}
