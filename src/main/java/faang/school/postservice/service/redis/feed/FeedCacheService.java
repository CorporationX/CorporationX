package faang.school.postservice.service.redis.feed;

import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.TreeSet;

public interface FeedCacheService {

    void saveOrUpdatePostAndAuthorInCache(RedisPost redisPost, RedisUser redisUser);

    TreeSet<PostDto> getNewsFeed(Long userId);

    void addPostInFeed(PostDto post);

    void deletePostFromFeed(PostDto postDto);

    void deletePostFromCache(RedisPost redisPost);

    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    void saveOrUpdatePostInCache(RedisPost redisPost);

    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    void addPostView(PostDto postDto);
}
