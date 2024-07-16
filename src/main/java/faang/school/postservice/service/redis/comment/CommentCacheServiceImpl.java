package faang.school.postservice.service.redis.comment;

import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.comment.DeleteCommentEvent;
import faang.school.postservice.event.comment.NewCommentEvent;
import faang.school.postservice.event.comment.UpdateCommentEvent;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.validator.redis.RedisPostValidator;
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
    private final RedisPostValidator postValidator;
    private final RedisCommentValidator commentValidator;

    @Value("${cache.max-comments}")
    private Integer maxCommentsInCache;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addCommentToPost(NewCommentEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getCommentDto().getPostId());
        redisPost = postValidator.validatePostExistence(event.getCommentDto().getPostId(), redisPost);

        redisPost.getRedisCommentsIds().add(event.getCommentDto().getId());
        redisPostRepository.save(redisPost.getId(), redisPost);
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void updateCommentOnPost(UpdateCommentEvent event) {
        RedisComment redisComment = redisCommentRepository.getById(event.getCommentDto().getId(), event.getCommentLikesIds());
        redisComment = commentValidator.validatePostExistence(event.getCommentDto().getId(), redisComment);

        redisComment.setCommentDto(event.getCommentDto());
        redisCommentRepository.save(redisComment.getId(), redisComment);
    }

    @Override
    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteCommentFromPost(DeleteCommentEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getCommentDto().getPostId());
        redisPost = postValidator.validatePostExistence(event.getCommentDto().getPostId(), redisPost);

        redisPost.getRedisCommentsIds().remove(event.getCommentDto().getId());
        redisPostRepository.save(redisPost.getId(), redisPost);
    }
}