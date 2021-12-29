package com.mycoder.community;

import com.mycoder.community.dao.DiscussPostMapper;
import com.mycoder.community.dao.LoginTicketMapper;
import com.mycoder.community.dao.MessageMapper;
import com.mycoder.community.dao.UserMapper;
import com.mycoder.community.entity.DiscussPost;
import com.mycoder.community.entity.LoginTicket;
import com.mycoder.community.entity.Message;
import com.mycoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author cj
 * @create 2021-12-08 21:27
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        User user1 = userMapper.selectById(101);
        User user2 = userMapper.selectByEmail("nowcoder101@sina.com");
        User user3 = userMapper.selectByName("liubei");

        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);
    }

    @Test
    public void testInsert(){
        User user = new User();
        user.setUserName("Test");
        user.setPassword("123456");
        user.setCreateTime(new Date());
        user.setHeaderUrl("www.baidu.com/111.jpg");
        user.setSalt("5esW");
        user.setEmail("151@qq.com");
        //返回行号
        int id = user.getId();
        System.out.println("插入前 id  " + id);

        int rows = userMapper.insertUser(user);
        id = user.getId();

        System.out.println("行号" + rows);
        System.out.println("插入后 id  " + id);
    }

    @Test
    public void test(){
        userMapper.updateStatus(150, 2);

        userMapper.updateHeader(150, "www.aliyun.com/119.gif");

        userMapper.updatePassword(150, "*******");
    }
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Test
    public void test1(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(10, 0, 10);

        for(DiscussPost post : list){
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(103);

        System.out.println(rows);
    }

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testLoginTicket1(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(1);
        loginTicket.setTicket("2334");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 15));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testLoginTicket2(){
        LoginTicket loginTicket = new LoginTicket();

        loginTicket = loginTicketMapper.selectByTicket("2334");
        System.out.println(loginTicket);

        int m = loginTicketMapper.updateStatus("2334", 1);
        System.out.println(m);

        loginTicket = loginTicketMapper.selectByTicket("2334");
        System.out.println(loginTicket);
    }

    @Autowired
    MessageMapper messageMapper;
    @Test
    public void testMessageMapper(){
        List<Message> like = messageMapper.selectNotices(155, "like", 0, 5);
        System.out.println(like.toString());
    }

}