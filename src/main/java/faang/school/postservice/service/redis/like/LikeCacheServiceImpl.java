package faang.school.postservice.service.redis.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.redis.comment.RedisCommentDto;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeCacheServiceImpl implements LikeCacheService {

    private final RedisPostRepository redisPostRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Retryable(
            value = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void addLikeOnPost(LikeDto likeDto) {
        redisPostRepository.findById(likeDto.getPostId()).ifPresent(redisPost -> {
            redisPost.likeIncrement();
            updateRedisPost(redisPost);
        });
    }

    @Override
    @Retryable(
            value = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void deleteLikeFromPost(long postId) {
        redisPostRepository.findById(postId).ifPresent(redisPost -> {
            redisPost.likeDecrement();
            updateRedisPost(redisPost);
        });
    }

    @Override
    @Retryable(
            value = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void addLikeToComment(long postId, long commentId) {
        redisPostRepository.findById(postId).ifPresent(redisPost -> {
            List<RedisCommentDto> comments = getCommentsList(redisPost);

            comments.stream()
                    .filter(comment -> comment.getId() == commentId)
                    .forEach(RedisCommentDto::likeIncrement);

            updateRedisPost(redisPost);
        });
    }

    @Override
    @Retryable(
            value = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void deleteLikeFromComment(long postId, long commentId) {
        redisPostRepository.findById(postId).ifPresent(redisPost -> {
            List<RedisCommentDto> comments = getCommentsList(redisPost);

            comments.stream()
                    .filter(comment -> comment.getId() == commentId)
                    .forEach(RedisCommentDto::likeDecrement);

            updateRedisPost(redisPost);
        });
    }

    private void updateRedisPost(Object redisPost) {
        redisTemplate.opsForValue().set(String.valueOf(redisPost.hashCode()), redisPost);
    }

    private List<RedisCommentDto> getCommentsList(RedisPost redisPost) {
        List<RedisCommentDto> comments = redisPost.getRedisCommentDtos();
        return comments != null ? comments : List.of();
    }
}
