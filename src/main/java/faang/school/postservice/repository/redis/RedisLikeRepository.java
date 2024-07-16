package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisLikeRepository {

    private final RedisTemplate<String, RedisUser> redisLikesTemplate;
    private static final String ZSET_KEY = "redisLikeZSet";

    public RedisUser getById(Long id) {
        Set<RedisUser> resultSet = redisLikesTemplate.opsForZSet().rangeByScore(ZSET_KEY, id, id);
        return resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisUser redisUser) {
        redisLikesTemplate.opsForZSet().add(ZSET_KEY, redisUser, id);
    }

    public void remove(Long id) {
        RedisUser redisUser = getById(id);
        if (redisUser != null) {
            redisLikesTemplate.opsForZSet().remove(ZSET_KEY, redisUser);
        }
    }
}