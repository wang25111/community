package com.mycoder.community.service;

import com.mycoder.community.dao.CommentMapper;
import com.mycoder.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cj
 * @create 2021-12-19 16:38
 */
@Service
public class CommentService {

    @Autowired
    CommentMapper commentMapper;

    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId){
        return commentMapper.selectCommentCount(entityType, entityId);
    }

}
