package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisCommentRepository {

    private final RedisTemplate<String, RedisComment> redisCommentsTemplate;
    private static final String ZSET_KEY = "redisCommentZSet";

    public RedisComment getById(Long id) {
        Set<RedisComment> resultSet = redisCommentsTemplate.opsForZSet().rangeByScore(ZSET_KEY, id, id);
        return resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisComment redisComment) {
        redisCommentsTemplate.opsForZSet().add(ZSET_KEY, redisComment, id);
    }

    public void update(Long id, RedisComment redisComment) {
        remove(id);
        save(id, redisComment);
    }

    public void remove(Long id) {
        RedisComment redisComment = getById(id);
        if (redisComment != null) {
            redisCommentsTemplate.opsForZSet().remove(ZSET_KEY, redisComment);
        }
    }
}