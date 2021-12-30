package com.mycoder.community.dao.elasticsearch;

import com.mycoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author cj
 * @create 2021-12-29 20:05
 */

@Repository                                                           //类型    ，   主键的类型
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
