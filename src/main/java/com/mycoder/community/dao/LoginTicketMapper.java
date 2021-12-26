package com.mycoder.community.dao;

import com.mycoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author cj
 * @create 2021-12-13 16:50
 * 对表login_ticket进行操作
 */
@Deprecated
@Mapper
public interface LoginTicketMapper {
    //插入凭证
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired )",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    int insertLoginTicket(LoginTicket loginTicket);

    //查询凭证
    @Select({
            "select id,user_id,ticket,status,expired from login_ticket ",
            "where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //删除（更新）凭证
    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket, int status);
}
