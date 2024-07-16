package faang.school.postservice.service.redis.like;

import faang.school.postservice.entity.dto.like.LikeDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.SortedSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeCacheServiceImpl implements LikeCacheService {

    private final RedisPostRepository redisPostRepository;
    private final PostRepository postRepository;
    private final RedisCommentRepository redisCommentRepository;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addLikeOnPost(LikeDto likeDto) {
        RedisPost redisPost = redisPostRepository.getById(likeDto.getPostId());
        if (redisPost == null) {
            log.error("Post not found for ID: {}", likeDto.getPostId());
            return;
        }

        redisPost.getPostDto().incrementLikes();
        redisPostRepository.save(redisPost.getId(), redisPost);
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromPost(LikeDto likeDto) {
        RedisPost redisPost = redisPostRepository.getById(likeDto.getPostId());
        if (redisPost == null) {
            log.error("Post not found for ID: {}", likeDto.getPostId());
            return;
        }

        redisPost.getPostDto().decrementLikes();
        redisPostRepository.save(redisPost.getId(), redisPost);
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addLikeToComment(long postId, long commentId) {
        RedisPost redisPost = redisPostRepository.getById(postId);
        if (redisPost == null) {
            log.error("Post not found for ID: {}", postId);
            return;
        }

        SortedSet<Long> commentIds = redisPost.getRedisCommentsIds();
        if (commentIds != null && commentIds.contains(commentId)) {
            RedisComment redisComment = redisCommentRepository.getById(commentId);
            if (redisComment != null) {
                redisComment.likeIncrement();
                redisCommentRepository.save(redisComment.getId(), redisComment);
            } else {
                log.error("Comment not found for ID: {}", commentId);
            }
        }
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromComment(long postId, long commentId) {
        RedisPost redisPost = redisPostRepository.getById(postId);
        if (redisPost == null) {
            log.error("Post not found for ID: {}", postId);
            return;
        }

        SortedSet<Long> commentIds = redisPost.getRedisCommentsIds();
        if (commentIds != null && commentIds.contains(commentId)) {
            RedisComment redisComment = redisCommentRepository.getById(commentId);
            if (redisComment != null) {
                redisComment.likeDecrement();
                redisCommentRepository.save(redisComment.getId(), redisComment);
            } else {
                log.error("Comment not found for ID: {}", commentId);
            }
        }
    }
}