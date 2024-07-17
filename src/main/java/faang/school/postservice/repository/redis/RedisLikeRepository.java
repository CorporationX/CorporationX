package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisLike;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisLikeRepository {

    private final ZSetOperations<String, RedisLike> likeZSetOps;
    private static final String ZSET_KEY = "redisLikeZSet";

    public RedisLike getById(Long id) {
        Set<RedisLike> resultSet = likeZSetOps.rangeByScore(ZSET_KEY, id, id);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisLike redisLike) {
        likeZSetOps.add(ZSET_KEY, redisLike, id);
    }

    public void remove(Long id) {
        RedisLike redisLike = getById(id);
        if (redisLike != null) {
            likeZSetOps.remove(ZSET_KEY, redisLike);
        }
    }
}