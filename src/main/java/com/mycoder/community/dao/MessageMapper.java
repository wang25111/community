package com.mycoder.community.dao;

import com.mycoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author cj
 * @create 2021-12-22 21:06
 */

@Mapper
public interface MessageMapper {

    //查询当前用户的会话列表，每个会话只返回最新的私信,支持分页
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话中的所有私信
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话中私信的数量
    int selectLetterCount(String conversationId);

    //查询某个用户的未读私信数量
    int selectLetterUnreadCount(int userId, String conversationId);

    //新增一条消息
    int insertMessage(Message message);

    //修改消息的状态, 将未读改成已读或将消息删除, ids为消息id的集合
    int updateStatus(List<Integer> ids, int status);

    //查找某用户的某主题最新一条消息
    Message selectLatestNotice(int userId, String topic);

    //查找某用户的某主题的所有数据
    int selectNoticeCount(int userId, String topic);

    //查找某用户的某主题（或所有）的未读消息
    int selectNoticeUnreadCount(int userId, String topic);

    //查询某一主题的所有通知信息
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
