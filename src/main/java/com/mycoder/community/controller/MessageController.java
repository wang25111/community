package com.mycoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.mycoder.community.entity.Message;
import com.mycoder.community.entity.Page;
import com.mycoder.community.entity.User;
import com.mycoder.community.service.MessageService;
import com.mycoder.community.service.UserService;
import com.mycoder.community.util.CommunityConstant;
import com.mycoder.community.util.CommunityUtil;
import com.mycoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author cj
 * @create 2021-12-23 13:26
 */
@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    /**会话列表*/
    @GetMapping("/letter/list")
    public String getLetters(Model model, Page page){
        User user = hostHolder.getUser();

        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //获取会话列表
        List<Message> conversations =
                messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> mapList = new ArrayList<>();

        //当前用户也可能没有会话
        if(conversations != null){
            for(Message message : conversations) {
                Map<String, Object> map = new HashMap<>();

                //每个会话中的最新消息
                map.put("conversation", message);
                //每个会话中的消息数量
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                //每个会话中的未读消息数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                //当前用户与哪个用户进行的会话
                int targetId = (user.getId() == message.getFromId() ? message.getToId() : message.getFromId());
                map.put("target", userService.findUserById(targetId));

                mapList.add(map);
            }
        }
        //会话列表中的信息
        model.addAttribute("mapList", mapList);

        //查询当前用户的所有未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        //传入所有未读通知消息数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }


    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(Model model, Page page, @PathVariable("conversationId") String conversationId){
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //获取所有消息
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters =  new ArrayList<>();

        if(letterList != null){
            for(Message letter : letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("fromUser", userService.findUserById(letter.getFromId()));

                letters.add(map);
            }
        }

        //私信详情
        model.addAttribute("letters", letters);
        //私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        /**设置消息为已读状态*/
        List<Integer> unreadIds = getUnreadLettersIds(letterList);
        if(!unreadIds.isEmpty()) messageService.readMessage(unreadIds);

        return "/site/letter-detail";
    }


    /**根据消息列表，获取未读消息的id， 供方法调用*/
    private List<Integer> getUnreadLettersIds(List<Message> messageList){
        User curUser = hostHolder.getUser();
        List<Integer> ids = new ArrayList<>();

        for(Message message : messageList){
            if(message.getToId() == curUser.getId() && message.getStatus() == 0){
                ids.add(message.getId());
            }
        }

        return ids;
    }


    /**根据会话id查询会话的目标用户，供上上条方法调用*/
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        User curUser = hostHolder.getUser();
        if(curUser.getId() == id0)
            return userService.findUserById(id1);
        else
            return userService.findUserById(id0);
    }


    @PostMapping("/letter/send")
    @ResponseBody
    private String sendLetter(String toName, String content){
        User targetUser = userService.findUserByName(toName);
        //如果要发送的目标不存在
        if(targetUser == null){
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }

        //设置消息属性: 内容、发送者、接收者......
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(targetUser.getId());
        message.setContent(content);
        message.setCreateTime(new Date());
        StringBuilder sb = new StringBuilder();
        if(message.getFromId() < message.getToId()){
            sb.append(message.getFromId());
            sb.append("_");
            sb.append(message.getToId());
        }else{
            sb.append(message.getToId());
            sb.append("_");
            sb.append(message.getFromId());
        }
        message.setConversationId(sb.toString());

        messageService.addMessage(message);
        //成功发送消息
        return CommunityUtil.getJSONString(0);
    }


    /**获取通知列表*/
    @GetMapping("/notice/list")
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        //评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVo = null;
        if(message != null){
            messageVo = new HashMap<>();
            messageVo.put("message", message);

            //将转义字符去掉, 并还原出(consumer中的)原始的map对象
            /**为什么不直接用还原出来的hashmap？？？*/
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap consumerMap = JSONObject.parseObject(content, HashMap.class);


            messageVo.put("user", userService.findUserById((Integer) consumerMap.get("userId")));//通知的触发者
            messageVo.put("entityType", consumerMap.get("entityType"));
            messageVo.put("entityId", consumerMap.get("entityId"));
            messageVo.put("postId", consumerMap.get("postId"));
            //所有消息数
            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count", count);
            //未读消息数
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unread", unread);
        }
        model.addAttribute("commentNotice", messageVo);

        //点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVo = null;
        if(message != null){
            messageVo = new HashMap<>();
            messageVo.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap consumerMap = JSONObject.parseObject(content, HashMap.class);

            //通知的触发者
            messageVo.put("user", userService.findUserById((Integer) consumerMap.get("userId")));
            messageVo.put("entityType", consumerMap.get("entityType"));
            messageVo.put("entityId", consumerMap.get("entityId"));
            messageVo.put("postId", consumerMap.get("postId"));
            //所有消息数
            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count", count);
            //未读消息数
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unread", unread);
        }
        model.addAttribute("likeNotice", messageVo);

        //关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVo = null;
        if(message != null){
            messageVo = new HashMap<>();
            messageVo.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap consumerMap = JSONObject.parseObject(content, HashMap.class);

            //通知的触发者
            messageVo.put("user", userService.findUserById((Integer) consumerMap.get("userId")));
            messageVo.put("entityType", consumerMap.get("entityType"));
            messageVo.put("entityId", consumerMap.get("entityId"));
            //所有消息数
            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count", count);
            //未读消息数
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unread", unread);
        }
        model.addAttribute("followNotice", messageVo);

        //传入所有未读私信消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        //传入所有未读通知消息数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }


    /**查询通知详情*/
    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model){
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if(noticeList != null){
            for(Message notice : noticeList){
                Map<String, Object> map = new HashMap<>();
                //通知本身
                map.put("notice", notice);
                //将message表中的content项的特殊字符去掉，然后转化为消费者中的hashmap
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                HashMap consumerMap = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) consumerMap.get("userId")));
                map.put("entityType", consumerMap.get("entityType"));
                map.put("entityId", consumerMap.get("entityId"));
                map.put("postId", consumerMap.get("postId"));
                //通知的作者，（系统？？？）
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }

        }
        model.addAttribute("notices", noticeVoList);

        //设置已读
        List<Integer> unreadList = getUnreadLettersIds(noticeList);
        if(!unreadList.isEmpty()){
            messageService.readMessage(unreadList);
        }

        return "/site/notice-detail";
    }
}
