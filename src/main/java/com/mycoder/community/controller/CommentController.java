package com.mycoder.community.controller;

import com.mycoder.community.entity.Comment;
import com.mycoder.community.entity.DiscussPost;
import com.mycoder.community.entity.Event;
import com.mycoder.community.event.EventProducer;
import com.mycoder.community.service.CommentService;
import com.mycoder.community.service.DiscussPostService;
import com.mycoder.community.util.CommunityConstant;
import com.mycoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author cj
 * @create 2021-12-19 21:27
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    DiscussPostService discussPostService;

    /**添加评论，专门针对帖子的评论？如何判断评论是给帖子的还是给评论的？ 并发布通知*/
    @PostMapping("/add/{postId}")
    public String addComment(Comment comment, @PathVariable("postId") int postId){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", postId);

        if(comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else{
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        //生产事件
        eventProducer.fireEvent(event);

        return "redirect:/discuss/detail/" + postId;
    }
}
