package faang.school.postservice.validator.redis;

import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLikeValidator {

    private final RedisPostRepository redisPostRepository;
    private final RedisCommentRepository redisCommentRepository;

    public RedisPost validatePostExists(Long postId) {
        RedisPost redisPost = redisPostRepository.getById(postId);
        if (redisPost == null) {
            log.error("Post not found for ID: {}", postId);
        }
        return redisPost;
    }

    public RedisComment validateCommentExists(Long commentId) {
        RedisComment redisComment = redisCommentRepository.getById(commentId);
        if (redisComment == null) {
            log.error("Comment not found for ID: {}", commentId);
        }
        return redisComment;
    }
}
