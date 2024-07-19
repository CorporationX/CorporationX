package faang.school.postservice.service.redis.like;

import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.like.DeleteCommentLikeEvent;
import faang.school.postservice.event.like.DeletePostLikeEvent;
import faang.school.postservice.event.like.LikeCommentEvent;
import faang.school.postservice.event.like.LikePostEvent;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.redis.CachedEntityBuilder;
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
    private final CachedEntityBuilder cachedEntityBuilder;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addLikeOnPost(LikePostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getLikeDto().getPostId());

        if (redisPost == null) {
            redisPost = cachedEntityBuilder.buildAndSaveNewRedisPost(event.getLikeDto().getPostId());
        }

        redisPost.getPostDto().getLikesIds().add(event.getLikeDto().getId());
        redisPostRepository.save(redisPost.getId(), redisPost);

        log.info("Added like {} to post {}", event.getLikeDto().getId(),
                event.getLikeDto().getPostId());
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromPost(DeletePostLikeEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getLikeDto().getPostId());

        if (redisPost == null) {
            redisPost = cachedEntityBuilder.buildAndSaveNewRedisPost(event.getLikeDto().getPostId());
        }

        redisPost.getPostDto().getLikesIds().remove(event.getLikeDto().getId());
        redisPostRepository.save(redisPost.getId(), redisPost);

        log.info("Deleted like {} from post {}", event.getLikeDto().getId(),
                event.getLikeDto().getPostId());
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addLikeToComment(LikeCommentEvent event) {
        RedisComment redisComment = redisCommentRepository.getById(event.getLikeDto().getCommentId());

        if (redisComment == null) {
            redisComment = cachedEntityBuilder.buildAndSaveNewRedisComment(event.getLikeDto().getCommentId());
        }

        redisComment.getRedisLikesIds().add(event.getLikeDto().getId());
        redisCommentRepository.save(redisComment.getId(), redisComment);

        log.info("Added like {} to comment {}", event.getLikeDto().getId(),
                event.getLikeDto().getCommentId());
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromComment(DeleteCommentLikeEvent event) {
        RedisComment redisComment = redisCommentRepository.getById(event.getLikeDto().getCommentId());

        if (redisComment == null) {
            redisComment = cachedEntityBuilder.buildAndSaveNewRedisComment(event.getLikeDto().getCommentId());
        }

        redisComment.getRedisLikesIds().remove(event.getLikeDto().getId());
        redisCommentRepository.save(redisComment.getId(), redisComment);

        log.info("Deleted like {} from comment {}", event.getLikeDto().getId(),
                event.getLikeDto().getCommentId());
    }
}
