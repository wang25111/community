package com.mycoder.community;

import com.mycoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author cj
 * @create 2021-12-17 18:03
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveFilterTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void test1(){
        String text = "论坛禁止发布与  ▼赌▼博▼ ◇放◇◇火◇  ♂吸¤毒♂ 相关话题帖子";
                text = sensitiveFilter.filter(text);
        System.out.println("---------------");
        System.out.println(text);
    }
}
