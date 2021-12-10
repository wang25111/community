package com.mycoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author cj
 * @create 2021-12-06 21:33
 */

@Repository("h")
public class HiberDao implements AlphaDao{
    @Override
    public String Select() {
        return "Hibernate";
    }
}
