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

    /**实体类型：帖子*/
    int ENTITY_TYPE_POST = 1;

    /**实体类型：评论*/
    int ENTITY_TYPE_COMMENT = 2;

    /**实体类型：用户*/
    int ENTITY_TYPE_USER = 3;

    /**主题：评论*/
    String TOPIC_COMMENT = "comment";

    /**主题：点赞*/
    String TOPIC_LIKE = "like";

    /**主题：关注*/
    String TOPIC_FOLLOW="follow";

    /**主题：发帖*/
    String TOPIC_PUBLISH="publish";

    /**系统用户id：1*/
    int SYSTEM_USER_ID = 1;

}
