package com.mycoder.community.service;

import com.mycoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author cj
 * @create 2021-12-24 20:14
 */
@Service
public class LikeService {
    @Autowired
    RedisTemplate redisTemplate;

    //点赞
    public void like(int userId, int entityType, int entityId, int entityUserId){

            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    String entityKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                    String userKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                    boolean isMember = redisTemplate.opsForSet().isMember(entityKey, userId);

                    operations.multi();
                    if(isMember){
                        //取消点赞
                        redisTemplate.opsForSet().remove(entityKey, userId);
                        redisTemplate.opsForValue().decrement(userKey);
                    }else{
                        //点赞
                        redisTemplate.opsForSet().add(entityKey, userId);
                        redisTemplate.opsForValue().increment(userKey);
                    }

                    return operations.exec();
                }
            });

    }


    //查询实体（帖子、评论）的点赞数量
    public long findEntityLikeCount(int entityType, int entityId){
        String entityKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityKey);
    }


    //查询某人对某实体的点赞状态，点赞返回1，未赞返回0
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityKey, userId) ? 1 : 0;
    }


    /**查询某个用户获得的赞*/
    public int findUserLikeCount(int userId){
        String userKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer userLikeCount = (Integer)redisTemplate.opsForValue().get(userKey);
        return userLikeCount == null ? 0 : userLikeCount;
    }
}
