package com.mycoder.community.util;

/**
 * @author cj
 * @create 2021-12-12 19:43
 */
public interface CommunityConstant {

    /**
     * 激活成功
     * */
    int ACTIVATION_SUCCESS = 0;

    /**
     *重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     * */
    int ACTIVATION_FAILURE = 2;

    /**未勾选记住我 的登录凭证超时时间: 12 h */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**勾选记住我 的登录凭证超时时间: 14 day*/
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 14;

    /**针对帖子的评论（评论）*/
    int ENTITY_TYPE_POST = 1;

    /**针对评论的评论（回复）*/
    int ENTITY_TYPE_COMMENT = 2;
}
