package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisPostRepository {

    private final RedisTemplate<String, RedisPost> redisPostsTemplate;
    private static final String ZSET_KEY = "redisPostZSet";

    public RedisPost getById(Long id) {
        Set<RedisPost> resultSet = redisPostsTemplate.opsForZSet().rangeByScore(ZSET_KEY, id, id);
        return resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisPost redisPost) {
        redisPostsTemplate.opsForZSet().add(ZSET_KEY, redisPost, id);
    }

    public void remove(Long id) {
        RedisPost redisPost = getById(id);
        if (redisPost != null) {
            redisPostsTemplate.opsForZSet().remove(ZSET_KEY, redisPost);
        }
    }
}