package com.mycoder.community.service;

import com.mycoder.community.dao.DiscussPostMapper;
import com.mycoder.community.entity.DiscussPost;
import com.mycoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author cj
 * @create 2021-12-09 18:03
 */
@Service
public class DiscussPostService {
    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilter filter;

    //按条件查询帖子
    public List<DiscussPost> findDiscussPosts(int userId, int offSet, int limit, int orderMode){
        return discussPostMapper.selectDiscussPosts(userId, offSet, limit, orderMode);
    }

    //按userId查询帖子数量
    public int findDiscussRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    //将帖子过滤后插入到表中
    public int addDiscussPost(DiscussPost discussPost){
        if(discussPost == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //转义帖子中的html标记字符
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        //过滤敏感词
        discussPost.setTitle(filter.filter(discussPost.getTitle()));
        discussPost.setContent(filter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPost(id);
    }

    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }


    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id, status);
    }


    public int updateType(int id, int type){
        return discussPostMapper.updateType(id, type);
    }

    /**更新帖子分数*/
    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id, score);
    }
}
