package com.mycoder.community.controller;

import com.mycoder.community.entity.Comment;
import com.mycoder.community.service.CommentService;
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
public class CommentController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @PostMapping("/add/{postId}")
    public String addComment(Comment comment, @PathVariable("postId") int postId){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + postId;
    }
}
