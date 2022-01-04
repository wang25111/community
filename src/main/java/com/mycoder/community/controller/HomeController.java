package com.mycoder.community.controller;

import com.mycoder.community.entity.DiscussPost;
import com.mycoder.community.entity.Page;
import com.mycoder.community.entity.User;
import com.mycoder.community.service.DiscussPostService;
import com.mycoder.community.service.LikeService;
import com.mycoder.community.service.UserService;
import com.mycoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cj
 * @create 2021-12-09 18:22
 */
@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    UserService userService;
    @Autowired
    DiscussPostService discussPostService;

    /**修改页面上的赞*/
    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){
        //SpringMVC会自动实例化model和 page，并且将page注入到model中
        page.setRows(discussPostService.findDiscussRows(0));
        page.setPath("/index?orderMode=" + orderMode);//防止切换页面时丢掉排序模式

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if(list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();

                map.put("post", post);

                User user = userService.findUserById(post.getUserId());
                map.put("user", user);

                //查询帖子的点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                //每个map保存帖子以及对应的用户
                discussPosts.add(map);
            }
        }
        //"discussPosts"用在index.html中
        model.addAttribute("discussPosts", discussPosts);
        //排序方式
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }

    /**权限不足时的处理*/
    @GetMapping("/denied")
    public String getDeniedPage(){
        return "/error/404";
    }


}
