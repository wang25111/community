package com.mycoder.community.dao;

import com.mycoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author cj
 * @create 2021-12-19 16:21
 */
@Mapper
public interface CommentMapper {

    //查询某一帖子（评论）的所有评论
    public List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //查询某一帖子（评论）的评论数量
    public int selectCommentCount(int entityType, int entityId);
}
