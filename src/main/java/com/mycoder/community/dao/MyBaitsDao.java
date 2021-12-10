package com.mycoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author cj
 * @create 2021-12-06 21:36
 */
@Repository
@Primary
public class MyBaitsDao implements AlphaDao{
    @Override
    public String Select() {
        return "Mybaits";
    }
}
