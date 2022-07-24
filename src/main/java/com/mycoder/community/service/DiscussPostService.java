package com.mycoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mycoder.community.dao.DiscussPostMapper;
import com.mycoder.community.entity.DiscussPost;
import com.mycoder.community.util.SensitiveFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author cj
 * @create 2021-12-09 18:03
 */
@Service
public class DiscussPostService {

    public static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilter filter;

    @Value("${caffeine.posts.max-size}")
    int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    int expireSeconds;

    //帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;


    @PostConstruct
    public void init(){
        //初始化帖子列表缓存

        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    //初始数据的来源
                    @Override
                    public  List<DiscussPost> load(String key) throws Exception {
                        if(key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2){
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);

                        //可以先访问redis

                        logger.debug("从【数据库】查询帖子列表-->初始化");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });


        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public Integer load(Integer key) throws Exception {

                        //可以先访问redis

                        logger.debug("从【数据库】查询帖子行数-->初始化");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }


    //按条件查询帖子
    public List<DiscussPost> findDiscussPosts(int userId, int offSet, int limit, int orderMode){
        //只有查首页的热帖时，才从本地缓存中取
        if(userId == 0 && orderMode == 1){
            logger.debug("从【本地缓存】查询帖子列表");
            return postListCache.get(offSet + ":" + limit);
        }

        logger.debug("从【数据库】查询帖子列表");
        return discussPostMapper.selectDiscussPosts(userId, offSet, limit, orderMode);
    }


    //按userId查询帖子数量
    public int findDiscussRows(int userId){
        //只有查首页时，才从本地缓存中取行数，且对应的key一直为0
        if(userId == 0){
            logger.debug("从【本地缓存】查询帖子数量");
            return postRowsCache.get(0);
        }

        logger.debug("从【数据库】查询帖子数量");
        return discussPostMapper.selectDiscussPostRows(userId);
    }


    //将帖子过滤后插入到表中
    public int addDiscussPost(DiscussPost discussPost){
        if(discussPost == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //转义帖子中的html标记字符
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        //过滤敏感词
        discussPost.setTitle(filter.filter(discussPost.getTitle()));
        discussPost.setContent(filter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }


    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPost(id);
    }


    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }


    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id, status);
    }


    public int updateType(int id, int type){
        return discussPostMapper.updateType(id, type);
    }


    /**更新帖子分数*/
    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id, score);
    }
}
