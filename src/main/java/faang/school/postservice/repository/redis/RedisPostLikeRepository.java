package faang.school.postservice.repository.redis;

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
public class RedisPostLikeRepository {

    private final ZSetOperations<String, RedisPostLike> postLikeZSetOps;
    private static final String ZSET_KEY = "redisPostLikeZSet";

    public RedisPostLike getById(Long id) {
        Set<RedisPostLike> resultSet = postLikeZSetOps.rangeByScore(ZSET_KEY, id, id);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    @Retryable(retryFor = { OptimisticLockingFailureException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void save(Long id, RedisPostLike redisPostLike) {
        RedisPostLike currentPostLike = getById(id);
        if (currentPostLike != null && !currentPostLike.getVersion().equals(redisPostLike.getVersion())) {
            throw new OptimisticLockingFailureException("Version conflict: current post like version "
                    + currentPostLike.getVersion() + " does not match " + redisPostLike.getVersion());        }
        redisPostLike.incrementVersion();
        postLikeZSetOps.add(ZSET_KEY, redisPostLike, id);
    }

    public void remove(Long id) {
        RedisPostLike redisCommentLike = getById(id);
        if (redisCommentLike != null) {
            postLikeZSetOps.remove(ZSET_KEY, redisCommentLike);
        }
    }
}