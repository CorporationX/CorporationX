package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisFeed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
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

    public void save(Long id, RedisFeed redisFeed) {
        feedZSetOps.add(ZSET_KEY, redisFeed, id);
    }

    public void remove(Long id) {
        RedisFeed redisFeed = getById(id);
        if (redisFeed != null) {
            feedZSetOps.remove(ZSET_KEY, redisFeed);
        }
    }
}