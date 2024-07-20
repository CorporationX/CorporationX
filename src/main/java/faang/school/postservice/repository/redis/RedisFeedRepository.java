package faang.school.postservice.repository.redis;

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
public class RedisFeedRepository {

    private final ZSetOperations<String, RedisFeed> feedZSetOps;
    private static final String ZSET_KEY = "redisFeed";

    public RedisFeed getById(Long userId) {
        Set<RedisFeed> resultSet = feedZSetOps.rangeByScore(ZSET_KEY, userId, userId);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    @Retryable(retryFor = { OptimisticLockingFailureException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void save(Long id, RedisFeed redisFeed) {
        RedisFeed currentFeed = getById(id);
        if (currentFeed != null && !currentFeed.getVersion().equals(redisFeed.getVersion())) {
            throw new OptimisticLockingFailureException("Version conflict: current feed version "
                    + currentFeed.getVersion() + " does not match " + redisFeed.getVersion());        }
        redisFeed.incrementVersion();
        feedZSetOps.add(ZSET_KEY, redisFeed, id);
    }

    public void remove(Long id) {
        RedisFeed redisFeed = getById(id);
        if (redisFeed != null) {
            feedZSetOps.remove(ZSET_KEY, redisFeed);
        }
    }
}
