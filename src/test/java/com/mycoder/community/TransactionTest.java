package com.mycoder.community;

import com.mycoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author cj
 * @create 2021-12-18 21:59
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTest {
    @Autowired
    AlphaService service;

    @Test
    public void test(){
        service.save1();
    }

    @Test
    public void test1(){
        service.save2();
    }


    @Test
    public void test2(){
        service.InsertA();
    }

}

