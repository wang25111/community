package com.mycoder.community.dao;

import com.mycoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cj
 * @create 2021-12-19 16:21
 */
@Mapper
public interface CommentMapper {

    //查询某一帖子（评论）的所有评论
    public List<Comment> selectCommentsByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId, @Param("offset") int offset, @Param("limit") int limit);

    //查询某一帖子（评论）的评论数量
    public int selectCommentCount(@Param("entityType") int entityType, @Param("entityId") int entityId);

    //插入一条评论
    public int insertComment(Comment comment);


    /**根据评论的id查询评论*/
    Comment selectCommentById(@Param("id") int id);
}
