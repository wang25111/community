package com.mycoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.mycoder.community.entity.Event;
import com.mycoder.community.entity.Message;
import com.mycoder.community.service.MessageService;
import com.mycoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cj
 * @create 2021-12-27 20:25
 */
@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    MessageService messageService;

    /**消费消息，将消息存入到message表中*/
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息为空！");
            return;
        }

        //将json字符串还原为对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息格式错误！");
            return;
        }

        //发送站内通知
        //对于message表，当用户from_id为1时，表示是系统消息，会话id存放消息主题，
        // content中存放json字符串，表示通知所依赖的各种信息，由一个map转化而来
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        //设置message content的前身
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());   //事件的触发者
        content.put("entityType", event.getEntityType());//事件的类型
        content.put("entityId", event.getEntityId());

        if(!event.getData().isEmpty()){
            for(Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }

        //生成message的内容（字符串格式），并插入
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }


}
