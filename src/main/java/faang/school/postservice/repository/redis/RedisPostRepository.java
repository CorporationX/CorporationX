package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.model.redis.RedisPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisPostRepository {

    private final ZSetOperations<String, RedisPost> postZSetOps;

    private final String ZSET_KEY = "redisPostZSet";

    public RedisPost getById(Long postId) {
        Set<RedisPost> resultSet = postZSetOps.rangeByScore(ZSET_KEY, postId, postId);
        return resultSet == null || resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public void save(Long id, RedisPost redisPost) {
        postZSetOps.add(ZSET_KEY, redisPost, id);
    }

    public void remove(Long id) {
        RedisPost redisPost = getById(id);
        if (redisPost != null) {
            postZSetOps.remove(ZSET_KEY, redisPost);
        }
    }
}