package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisCommentRepository {

    private final ZSetOperations<String, RedisComment> commentZSetOps;

    private final String ZSET_KEY = "redisCommentZSet";

    public RedisComment getById(Long commentId) {
        Set<RedisComment> resultSet = commentZSetOps.rangeByScore(ZSET_KEY, commentId, commentId);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisComment redisComment) {
        commentZSetOps.add(ZSET_KEY, redisComment, id);
    }

    public void remove(Long id) {
        RedisComment redisComment = getById(id);
        if (redisComment != null) {
            commentZSetOps.remove(ZSET_KEY, redisComment);
        }
    }
}
