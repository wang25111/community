package com.mycoder.community.quartz;

import com.mycoder.community.entity.DiscussPost;
import com.mycoder.community.service.DiscussPostService;
import com.mycoder.community.service.ElasticsearchService;
import com.mycoder.community.service.LikeService;
import com.mycoder.community.util.CommunityConstant;
import com.mycoder.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
    任务：定时刷新帖子
 */
public class PostScoreRefreshJob implements Job, CommunityConstant {

    public static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private LikeService likeService;

    private static final Date epcho;
    //初始化：纪元
    static {
        try {
            epcho = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-08 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化纪元失败！");
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if(operations.size() == 0){
            logger.info("【任务取消】无帖子需要刷新");
            return;
        }

        logger.info("【任务开始】");
        while(operations.size() > 0){
            this.refresh((Integer)operations.pop());
        }
        logger.info("【任务结束】刷新完毕");
    }

    private void refresh(int postId){
        DiscussPost post = discussPostService.findDiscussPostById(postId);

        if(post == null){
            logger.info("帖子不存在，帖子id = " + postId);
        }
        //权重：
            //是否精华
        boolean wonderful = post.getStatus() == 1;
            //评论数量
        int commentCount = post.getCommentCount();
            //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

            //计算权重
        double weight = (wonderful ? 50 : 0) + commentCount * 10 + likeCount * 8;
        //分数 = 权重 + 距离天数
        double score = Math.log10(weight + 1)
                + (post.getCreateTime().getTime() - epcho.getTime()) / (1000 * 3600 * 24);

        //更新分数
        discussPostService.updateScore(postId, score);
        //ES服务器同步搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);

    }
}
