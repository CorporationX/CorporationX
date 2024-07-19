package faang.school.postservice.service.redis.comment;

import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.comment.DeleteCommentEvent;
import faang.school.postservice.event.comment.NewCommentEvent;
import faang.school.postservice.event.comment.UpdateCommentEvent;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.redis.CachedEntityBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCacheServiceImpl implements CommentCacheService {

    private final RedisPostRepository redisPostRepository;
    private final RedisCommentRepository redisCommentRepository;
    private final CachedEntityBuilder cachedEntityBuilder;

    @Value("${cache.max-comments}")
    private Integer maxCommentsInCache;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addCommentToPost(NewCommentEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getCommentDto().getPostId());

        if (redisPost == null) {
            redisPost = cachedEntityBuilder.buildAndSaveNewRedisPost(event.getCommentDto().getPostId());
        }

        if (redisPost.getRedisCommentsIds().size() >= maxCommentsInCache) {
            redisPost.getRedisCommentsIds().remove(redisPost.getRedisCommentsIds().first());
        }

        redisPost.getRedisCommentsIds().add(event.getCommentDto().getId());
        redisPostRepository.save(redisPost.getId(), redisPost);
        log.info("Added new comment {} to post {}", event.getCommentDto().getId(), event.getCommentDto().getPostId());
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void updateCommentOnPost(UpdateCommentEvent event) {
        RedisComment redisComment = redisCommentRepository.getById(event.getCommentDto().getId());

        if (redisComment == null) {
            redisComment = cachedEntityBuilder.buildAndSaveNewRedisComment(event.getCommentDto().getId());
        }

        redisComment.setCommentDto(event.getCommentDto());
        redisCommentRepository.save(redisComment.getId(), redisComment);
        log.info("Updated comment {} on post {}", event.getCommentDto().getId(), event.getCommentDto().getPostId());
    }

    @Override
    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteCommentFromPost(DeleteCommentEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getCommentDto().getPostId());

        redisPost.getRedisCommentsIds().remove(event.getCommentDto().getId());
        redisPostRepository.save(redisPost.getId(), redisPost);
        log.info("Deleted comment {} from post {}", event.getCommentDto().getId(), event.getCommentDto().getPostId());
    }
}