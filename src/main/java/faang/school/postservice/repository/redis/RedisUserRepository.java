package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisUserRepository {

    private final RedisTemplate<String, RedisUser> redisUsersTemplate;
    private static final String ZSET_KEY = "redisUserZSet";

    public RedisUser getById(Long id) {
        Set<RedisUser> resultSet = redisUsersTemplate.opsForZSet().rangeByScore(ZSET_KEY, id, id);
        return resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisUser redisUser) {
        redisUsersTemplate.opsForZSet().add(ZSET_KEY, redisUser, id);
    }

    public void remove(Long id) {
        RedisUser redisUser = getById(id);
        if (redisUser != null) {
            redisUsersTemplate.opsForZSet().remove(ZSET_KEY, redisUser);
        }
    }

    public void update(Long id, RedisUser redisUser) {
        remove(id);
        save(id, redisUser);
    }
}