package faang.school.postservice.service.redis.comment;

import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.comment.DeleteCommentEvent;
import faang.school.postservice.event.comment.NewCommentEvent;
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

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCacheServiceImpl implements CommentCacheService {

    private final RedisPostRepository redisPostRepository;
    private final CachedEntityBuilder cachedEntityBuilder;

    @Value("${cache.max-comments}")
    private Integer maxCommentsInCache;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addCommentToPost(NewCommentEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostId());

        if (redisPost == null) {
            redisPost = cachedEntityBuilder.savePostToRedis(event.getPostId());
        }

        Set<Long> commentsIds = redisPost.getCommentsIds();
        if (commentsIds.size() >= maxCommentsInCache) {
            commentsIds.remove(commentsIds.iterator().next());
        }

        commentsIds.add(event.getCommentId());
        redisPostRepository.save(redisPost.getId(), redisPost);
        log.info("Added new comment {} to post {}", event.getCommentId(), event.getPostId());
    }

    @Override
    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteCommentFromPost(DeleteCommentEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getCommentDto().getPostId());

        if (redisPost != null) {
            redisPost.getCommentsIds().remove(event.getCommentDto().getId());
            redisPostRepository.save(redisPost.getId(), redisPost);
            log.info("Deleted comment {} from post {}", event.getCommentDto().getId(), event.getCommentDto().getPostId());
        }
    }
}
