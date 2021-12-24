package com.mycoder.community;

import com.mycoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author cj
 * @create 2021-12-10 20:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;
    @Test
    public void testSend(){
        mailClient.sendMail("2511183459@qq.com","TEST", "测试成功！");
    }

    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testHtml(){
        Context context = new Context();
        context.setVariable("username", "管理员");

        String content = templateEngine.process("/mail/demo", context);

        mailClient.sendMail("2511183459@qq.com","TestHtml", content);
        System.out.println(content);
    }
}
