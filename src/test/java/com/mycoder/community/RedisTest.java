package com.mycoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author cj
 * @create 2021-12-24 16:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test1(){
        String rkey = "test:count";

        redisTemplate.opsForValue().set(rkey, 1);
        System.out.println(redisTemplate.opsForValue().increment(rkey));
        System.out.println(redisTemplate.opsForValue().get(rkey));
    }

    @Test
    public void testBoundZ(){
        String rkey = "test:count";
        BoundValueOperations ops = redisTemplate.boundValueOps(rkey);
        ops.increment(2);
        ops.increment(3);
        System.out.println(ops.get());

    }
}
