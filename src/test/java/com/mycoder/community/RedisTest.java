package com.mycoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

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

    /**有20万的数据，统计不重复的总数*/
    @Test
    public void test2(){
        String redisKey = "test:hll:01";

        for(int i = 1; i <= 1000; i++){
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }

        for (int i = 1; i <= 1000; i++){
            int num = new Random().nextInt(1000);
            redisTemplate.opsForHyperLogLog().add(redisKey, num);
        }

        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
    }

    /**数据合并*/
    @Test
    public void test3(){
        String redisKey2 = "test:hll:02";

        for(int i = 501; i <= 1500; i++){
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }

       String unionKey = "test:union";

        redisTemplate.opsForHyperLogLog().union(unionKey, redisKey2, "test:hll:01");

        System.out.println(redisTemplate.opsForHyperLogLog().size(unionKey));
    }

    /**bitmap*/
    @Test
    public void testBitMap1(){
        String redisKey = "test:bm:01";
        //记录
        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        redisTemplate.opsForValue().setBit(redisKey, 3, true);
        redisTemplate.opsForValue().setBit(redisKey, 5, true);
        //查询
        System.out.print(redisTemplate.opsForValue().getBit(redisKey,5));
        System.out.print(redisTemplate.opsForValue().getBit(redisKey,4));
        System.out.print(redisTemplate.opsForValue().getBit(redisKey,3));
        System.out.print(redisTemplate.opsForValue().getBit(redisKey,2));
        System.out.print(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));

        //统计1的个数
        Object o = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.bitCount(redisKey.getBytes());//传入键的字节
            }
        });

        System.out.println(o);
    }

    /**bitmap进行or运算*/

}
