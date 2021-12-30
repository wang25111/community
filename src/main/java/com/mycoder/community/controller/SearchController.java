package com.mycoder.community.controller;

import com.mycoder.community.entity.DiscussPost;
import com.mycoder.community.entity.Page;
import com.mycoder.community.service.ElasticsearchService;
import com.mycoder.community.service.LikeService;
import com.mycoder.community.service.UserService;
import com.mycoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cj
 * @create 2021-12-29 22:18
 */

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model){

        //获取查询结果
        org.springframework.data.domain.Page<DiscussPost> searchPosts =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        //设置分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchPosts == null ? 0 : (int) searchPosts.getTotalElements());

        //要返回给页面的信息
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(searchPosts != null){
            for(DiscussPost post : searchPosts){
                Map<String, Object> map = new HashMap<>();
                //查询到，且处理过的帖子
                map.put("post", post);
                //帖子的作者
                map.put("user", userService.findUserById(post.getUserId()));
                //帖子的点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }

            model.addAttribute("discussPosts", discussPosts);
            model.addAttribute("keyword", keyword);

        }

        return "/site/search";
    }

}
