package com.mycoder.community;

import com.mycoder.community.dao.MessageMapper;
import com.mycoder.community.entity.Message;
import com.mycoder.community.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cj
 * @create 2021-12-22 21:58
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MessageTest {

    @Autowired
    MessageMapper messageMapper;

    @Test
    public void test1(){
        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);
    }

    @Test
    public void test2(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for(Message m : messages){
            System.out.println(m);
        }
    }

    @Test
    public void test3(){
        int count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);
    }

    @Test
    public void test4(){
        List<Message> list = messageMapper.selectLetters("111_112", 0, 10);

        for(Message m : list){
            System.out.println(m);
        }
    }

    @Test
    public void test5(){
        int count = messageMapper.selectLetterUnreadCount(111, null);
        System.out.println(count);
    }

    @Autowired
    private MessageService messageService;

    @Test
    public void test6(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(356);
        arrayList.add(355);
        arrayList.add(359);

        //int i = messageMapper.updateStatus(arrayList, 0);

        int i = messageService.readMessage(arrayList);
        System.out.println(i);
    }

}
