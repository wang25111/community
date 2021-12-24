package com.mycoder.community;

import com.mycoder.community.dao.AlphaDao;
import com.mycoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;


//演示IOC相关知识点
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;//定义一个容器

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testIOC(){
        System.out.println(applicationContext);//打印容器

        AlphaDao dao = applicationContext.getBean("h",AlphaDao.class);
        System.out.println(dao.Select());
    }

    @Test
    public void test2(){
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);

        alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
    }

    @Test
    public void test3(){
        SimpleDateFormat bean = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(bean.format(new Date()));
    }

    //以上为通过IOC容器主动获取
    //以下为通过依赖注入获取
    @Autowired
    private AlphaService bean1;

    @Autowired
    private AlphaDao bean2;
    @Autowired
    @Qualifier("h")
    private AlphaDao bean3;

    @Test
    public void testDI(){
        System.out.println(bean1);
        System.out.println(bean2.Select());
        System.out.println(bean3.Select());
    }
}
