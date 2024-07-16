package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisLike;
import faang.school.postservice.entity.model.redis.RedisUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisLikeRepository {

    private final RedisTemplate<String, RedisLike> redisLikesTemplate;
    private static final String ZSET_KEY = "redisLikeZSet";

    public RedisLike getById(Long id) {
        Set<RedisLike> resultSet = redisLikesTemplate.opsForZSet().rangeByScore(ZSET_KEY, id, id);
        return resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisLike redisLike) {
        redisLikesTemplate.opsForZSet().add(ZSET_KEY, redisLike, id);
    }

    public void remove(Long id) {
        RedisLike redisLike = getById(id);
        if (redisLike != null) {
            redisLikesTemplate.opsForZSet().remove(ZSET_KEY, redisLike);
        }
    }
}