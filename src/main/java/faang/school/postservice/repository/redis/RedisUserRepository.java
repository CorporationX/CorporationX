package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisUserRepository {

    private final ZSetOperations<String, RedisUser> userZSetOps;
    private static final String ZSET_KEY = "redisUserZSet";

    public RedisUser getById(Long id) {
        Set<RedisUser> resultSet = userZSetOps.rangeByScore(ZSET_KEY, id, id);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisUser redisUser) {
        userZSetOps.add(ZSET_KEY, redisUser, id);
    }

    public void remove(Long id) {
        RedisUser redisUser = getById(id);
        if (redisUser != null) {
            userZSetOps.remove(ZSET_KEY, redisUser);
        }
    }

    public void update(Long id, RedisUser redisUser) {
        remove(id);
        save(id, redisUser);
    }
}