package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisComment;
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
public class RedisCommentRepository {

    private final ZSetOperations<String, RedisComment> commentZSetOps;

    private final String ZSET_KEY = "redisCommentZSet";

    public RedisComment getById(Long commentId) {
        Set<RedisComment> resultSet = commentZSetOps.rangeByScore(ZSET_KEY, commentId, commentId);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    @Retryable(retryFor = { OptimisticLockingFailureException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void save(Long id, RedisComment redisComment) {
        RedisComment currentComment = getById(id);
        if (currentComment != null && !currentComment.getVersion().equals(redisComment.getVersion())) {
            throw new OptimisticLockingFailureException("Version conflict: current comment version "
                    + currentComment.getVersion() + " does not match " + redisComment.getVersion());        }
        redisComment.incrementVersion();
        commentZSetOps.add(ZSET_KEY, redisComment, id);
    }

    public void remove(Long id) {
        RedisComment redisComment = getById(id);
        if (redisComment != null) {
            commentZSetOps.remove(ZSET_KEY, redisComment);
        }
    }
}
