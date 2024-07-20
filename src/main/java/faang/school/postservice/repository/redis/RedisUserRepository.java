package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisUser;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisUserRepository {

    private final ZSetOperations<String, RedisUser> userZSetOps;
    private final String ZSET_KEY = "redisUserZSet";

    public RedisUser getById(Long id) {
        Set<RedisUser> resultSet = userZSetOps.rangeByScore(ZSET_KEY, id, id);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    @Retryable(
            retryFor = { OptimisticLockingFailureException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 500, multiplier = 3)
    )
    public void save(Long id, RedisUser redisUser) {
        RedisUser currentRedisUser = getById(id);
        if (currentRedisUser != null && !currentRedisUser.getVersion().equals(redisUser.getVersion())) {
            throw new OptimisticLockingFailureException("Version conflict: current user version "
                    + currentRedisUser.getVersion() + " does not match " + redisUser.getVersion());
        }
        redisUser.incrementVersion();
        userZSetOps.add(ZSET_KEY, redisUser, id);
    }

    public void remove(Long id) {
        RedisUser redisUser = getById(id);
        if (redisUser != null) {
            userZSetOps.remove(ZSET_KEY, redisUser);
        }
    }

    public void update(Long id, RedisUser redisUser) {
        redisUser.incrementVersion();
        userZSetOps.add(ZSET_KEY, redisUser, id);
    }
}