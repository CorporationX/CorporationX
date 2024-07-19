package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisCommentLike;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisLikeRepository {

    private final ZSetOperations<String, RedisCommentLike> likeZSetOps;
    private static final String ZSET_KEY = "redisLikeZSet";

    public RedisCommentLike getById(Long id) {
        Set<RedisCommentLike> resultSet = likeZSetOps.rangeByScore(ZSET_KEY, id, id);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisCommentLike redisCommentLike) {
        likeZSetOps.add(ZSET_KEY, redisCommentLike, id);
    }

    public void remove(Long id) {
        RedisCommentLike redisCommentLike = getById(id);
        if (redisCommentLike != null) {
            likeZSetOps.remove(ZSET_KEY, redisCommentLike);
        }
    }
}