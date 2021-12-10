package com.mycoder.community.service;

import com.mycoder.community.dao.DiscussPostMapper;
import com.mycoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cj
 * @create 2021-12-09 18:03
 */
@Service
public class DiscussPostService {
    @Autowired
    DiscussPostMapper discussPostMapper;
    //按条件查询帖子
    public List<DiscussPost> findDiscussPosts(int userId, int offSet, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offSet, limit);
    }
    //按userId查询帖子数量
    public int findDiscussRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
