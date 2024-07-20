package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisCommentLike;
import faang.school.postservice.entity.model.redis.RedisFeed;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisCommentLikeRepository {

    private final ZSetOperations<String, RedisCommentLike> commentLikeZSetOps;
    private static final String ZSET_KEY = "redisCommentLikeZSet";

    public RedisCommentLike getById(Long id) {
        Set<RedisCommentLike> resultSet = commentLikeZSetOps.rangeByScore(ZSET_KEY, id, id);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    @Retryable(retryFor = { OptimisticLockingFailureException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void save(Long id, RedisCommentLike redisCommentLike) {
        RedisCommentLike currentCommentLike = getById(id);
        if (currentCommentLike != null && !currentCommentLike.getVersion().equals(redisCommentLike.getVersion())) {
            throw new OptimisticLockingFailureException("Version conflict: current comment like version "
                    + currentCommentLike.getVersion() + " does not match " + redisCommentLike.getVersion());        }
        redisCommentLike.incrementVersion();
        commentLikeZSetOps.add(ZSET_KEY, redisCommentLike, id);
    }

    public void remove(Long id) {
        RedisCommentLike redisCommentLike = getById(id);
        if (redisCommentLike != null) {
            commentLikeZSetOps.remove(ZSET_KEY, redisCommentLike);
        }
    }
}