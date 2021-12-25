package com.mycoder.community.util;

/**
 * @author cj
 * @create 2021-12-24 19:44
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE ="like:entity";
    private static final String PREFIX_USER_LIKE ="like:user";
    //(发起)关注的人
    private static final String PREFIX_FOLLOWER = "follower";
    //被关注的实体（用户、帖子...）
    private static final String PREFIX_FOLLOWEE = "followee";

    //某个实体的赞
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //某个用户收到的赞
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
    //某用户关注的实体（key 前缀:用户id:实体类型 ; value 实体id）
    //用zset存储，key-> followee:userId:entityType ，value-> entityId*/
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
    //某个实体拥有的关注用户(key  前缀:实体类型:实体id;  value 用户id)
    //用zset存储，key-> follower:entityType:entityId, value-> userId*/
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }
}
