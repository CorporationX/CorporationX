package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.SortedSet;

@Repository
@RequiredArgsConstructor
public class RedisCommentRepository {

    private final RedisTemplate<String, RedisComment> redisCommentsTemplate;
    private final CommentService commentService;
    private static final String ZSET_KEY = "redisCommentZSet";

    public RedisComment getById(Long commentId) {
        Set<RedisComment> resultSet = redisCommentsTemplate.opsForZSet().rangeByScore(ZSET_KEY, commentId, commentId);
        return resultSet.isEmpty() ? null : resultSet.iterator().next();
    }

    public RedisComment getById(Long commentId, SortedSet<Long> redisLikesIds) {
        Set<RedisComment> resultSet = redisCommentsTemplate.opsForZSet().rangeByScore(ZSET_KEY, commentId, commentId);
        if (resultSet == null || resultSet.isEmpty()) {
            return buildNewRedisComment(commentId, redisLikesIds);
        } else {
            return resultSet.iterator().next();
        }
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

    private RedisComment buildNewRedisComment(Long commentId, SortedSet<Long> redisLikesIds) {
        CommentDto commentDto = commentService.getById(commentId);
        RedisComment redisComment = RedisComment.builder()
                .id(commentId)
                .commentDto(commentDto)
                .redisLikesIds(redisLikesIds)
                .version(1L)
                .build();
        save(redisComment.getId(), redisComment);
        return redisComment;
    }
}