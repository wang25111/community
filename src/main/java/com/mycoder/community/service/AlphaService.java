package com.mycoder.community.service;

import com.mycoder.community.dao.AlphaDao;
import com.mycoder.community.dao.DiscussPostMapper;
import com.mycoder.community.dao.UserMapper;
import com.mycoder.community.entity.DiscussPost;
import com.mycoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * @author cj
 * @create 2021-12-06 21:49
 */
@Service
@Scope("prototype")
public class AlphaService {

    public AlphaService(){
        System.out.println("构造方法调用了--");
    }

    @PostConstruct
    public void init(){
        System.out.println("init（）方法调用");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("destroy（）调用==");
    }

    @Autowired
     @Qualifier("h")
    AlphaDao dao;

    public String find(){

        return dao.Select();
        
        
        
        

    }

    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper postMapper;
    /**模拟业务：新建一个用户，并且发一个帖子*/
    //设置隔离级别，传播机制
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //step1
        User user = new User();
        user.setUserName("夏明");
        user.setSalt("sdf7");
        user.setCreateTime(new Date());
        user.setPassword("123456");
        userMapper.insertUser(user);

        int a = 3 / 0;

        //step2
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("事务测试");
        discussPost.setContent("111111111111111");
        discussPost.setUserId(user.getId());
        postMapper.insertDiscussPost(discussPost);

        return "ok";

    }

    @Autowired
    private TransactionTemplate transactionTemplate;

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                //step1
                User user = new User();
                user.setUserName("小明");
                user.setSalt("sdf7");
                user.setCreateTime(new Date());
                user.setPassword("123456");
                userMapper.insertUser(user);

                int a = 3 / 0;

                //step2
                DiscussPost discussPost = new DiscussPost();
                discussPost.setTitle("事务测试");
                discussPost.setContent("111111111111111");
                discussPost.setUserId(user.getId());
                postMapper.insertDiscussPost(discussPost);

                return "ok";
            }
        });
    }


    /*==================以下代码测试spring事务的传播机制===========================**/
    //required
    //①A无事务，B无事务： 方法A插入成功，方法B1插入成功，方法B2插入失败
    //①A无事务，B有事务：  方法A插入成功，方法B1、方法B2插入失败
    //①A有事务，B有事务：   均插入失败
    //①A有事务，B无事务：   均插入失败

    @Autowired
    BeltaService beltaService;

//    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void InsertA(){
        User user = new User();
        user.setUserName("方法A");
        user.setSalt("sdf7");
        user.setCreateTime(new Date());
        user.setPassword("123456");
        userMapper.insertUser(user);

        //方法A调用方法B
        beltaService.insertB();
    }



}
