package com.mycoder.community;

import com.mycoder.community.util.CommunityUtil;
import org.junit.Test;

/**
 * @author cj
 * @create 2021-12-13 21:36
 */
public class MyTest {

    @Test
    public void test1(){
        String s = CommunityUtil.md5("123456" + "d4c1c");
        System.out.println(s);
    }
}
