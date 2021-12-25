package com.mycoder.community.controller;

import com.mycoder.community.entity.User;
import com.mycoder.community.service.LikeService;
import com.mycoder.community.util.CommunityUtil;
import com.mycoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cj
 * @create 2021-12-24 20:27
 */
@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @PostMapping("like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId){
        User user = hostHolder.getUser();

        //点赞或取消点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        //更新点赞数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);

        //更新点赞状态
        int likeStatus = likeService.findEntityLikeStatus(user.getStatus(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0, null, map);
    }
}
