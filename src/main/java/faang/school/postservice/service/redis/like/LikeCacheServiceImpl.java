package faang.school.postservice.service.redis.like;

import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.like.DeleteCommentLikeEvent;
import faang.school.postservice.event.like.DeletePostLikeEvent;
import faang.school.postservice.event.like.LikeCommentEvent;
import faang.school.postservice.event.like.LikePostEvent;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.validator.redis.RedisPostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeCacheServiceImpl implements LikeCacheService {

    private final RedisPostRepository redisPostRepository;
    private final RedisCommentRepository redisCommentRepository;
    private final RedisPostValidator postValidator;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addLikeOnPost(LikePostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getLikeDto().getPostId());
        redisPost = postValidator.validatePostExistence(event.getLikeDto().getPostId(), redisPost);

        redisPost.getRedisLikesIds().add(event.getLikeDto().getId());
        redisPostRepository.save(redisPost.getId(), redisPost);
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromPost(DeletePostLikeEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getLikeDto().getPostId());
        redisPost = postValidator.validatePostExistence(event.getLikeDto().getPostId(), redisPost);

        redisPost.getRedisLikesIds().remove(event.getLikeDto().getId());
        redisPostRepository.save(redisPost.getId(), redisPost);
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addLikeToComment(LikeCommentEvent event) {
        RedisComment redisComment = redisCommentRepository.getById(
                event.getLikeDto().getCommentId(),
                event.getCommentLikesIds());

        redisComment.getRedisLikesIds().add(event.getLikeDto().getId());
        redisCommentRepository.save(redisComment.getId(), redisComment);
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromComment(DeleteCommentLikeEvent event) {
        RedisComment redisComment = redisCommentRepository.getById(event.getLikeDto().getCommentId(), event.getCommentLikesIds());

        redisComment.getRedisLikesIds().add(event.getLikeDto().getId());
        redisCommentRepository.save(redisComment.getId(), redisComment);
    }
}