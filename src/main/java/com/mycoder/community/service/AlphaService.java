package com.mycoder.community.service;

import com.mycoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
}
