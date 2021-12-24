package com.mycoder.community;

import com.mycoder.community.dao.DiscussPostMapper;
import com.mycoder.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author cj
 * @create 2021-12-18 14:49
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class DiscussPostTest {
    @Autowired
    private DiscussPostMapper mapper;

    @Test
    public void test1(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("测试帖");
        discussPost.setContent("11111111111111222222222222223333333333333成功了");
        discussPost.setUserId(154);

        mapper.insertDiscussPost(discussPost);
    }

}
