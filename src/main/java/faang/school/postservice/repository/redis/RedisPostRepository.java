package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisPostLike;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisPostRepository {

    private final ZSetOperations<String, RedisPost> postZSetOps;

    private final String ZSET_KEY = "redisPostZSet";

    public RedisPost getById(Long postId) {
        Set<RedisPost> resultSet = postZSetOps.rangeByScore(ZSET_KEY, postId, postId);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    @Retryable(retryFor = { OptimisticLockingFailureException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void save(Long id, RedisPost redisPost) {
        RedisPost currentPost = getById(id);
        if (currentPost != null && !currentPost.getVersion().equals(redisPost.getVersion())) {
            throw new OptimisticLockingFailureException("Version conflict: current post version "
                    + currentPost.getVersion() + " does not match " + redisPost.getVersion());        }
        redisPost.incrementVersion();
        postZSetOps.add(ZSET_KEY, redisPost, id);
    }

    public void remove(Long id) {
        RedisPost redisPost = getById(id);
        if (redisPost != null) {
            postZSetOps.remove(ZSET_KEY, redisPost);
        }
    }
}