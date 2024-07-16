package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisFeed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisFeedRepository {

    private final RedisTemplate<String, RedisFeed> redisFeedTemplate;
    private static final String ZSET_KEY = "redisFeedZSet";

    public RedisFeed getById(Long id) {
        Set<RedisFeed> resultSet = redisFeedTemplate.opsForZSet().rangeByScore(ZSET_KEY, id, id);
        return resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisFeed redisFeed) {
        redisFeedTemplate.opsForZSet().add(ZSET_KEY, redisFeed, id);
    }

    public void remove(Long id) {
        RedisFeed redisFeed = getById(id);
        if (redisFeed != null) {
            redisFeedTemplate.opsForZSet().remove(ZSET_KEY, redisFeed);
        }
    }
}