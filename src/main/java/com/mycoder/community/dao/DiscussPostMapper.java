package com.mycoder.community.dao;

import com.mycoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cj
 * @create 2021-12-09 16:30
 */
@Mapper
public interface DiscussPostMapper {
    //查询贴子数，offset：开始行号
    //支持两种查询方式：id=0普通查询；id>=1 表示按用户id查询。
    //支持两种排序模式：orderMode=0普通排序，orderMode=1按帖子分数（热度排序）
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    //查询贴子数量，支持两种方式查询
    int selectDiscussPostRows(@Param("userId") int userId);

    //插入帖子
    int insertDiscussPost(DiscussPost discussPost);

    /**根据帖子的id查询帖子*/
    DiscussPost selectDiscussPost(int id);

    /**根据帖子的id，更新帖子的评论数量*/
    int updateCommentCount(int id, int commentCount);

    /**更改帖子的类型: 置顶*/
    int updateType(int id, int type);

    /**更改帖子的状态：加精或拉黑*/
    int updateStatus(int id, int status);

    /**更改帖子的分数*/
    int updateScore(int id, double score);
}
