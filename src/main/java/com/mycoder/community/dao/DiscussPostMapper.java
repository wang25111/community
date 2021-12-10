package com.mycoder.community.dao;

import com.mycoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author cj
 * @create 2021-12-09 16:30
 */
@Mapper
public interface DiscussPostMapper {
    //查询贴子数，offset：开始行号
    //支持两种查询方式：id=0普通查询；id>=1 表示按用户id查询。
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //查询贴子数量，支持两种方式查询
    int selectDiscussPostRows(int userId);

}
