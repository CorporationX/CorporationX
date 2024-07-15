package faang.school.postservice.service.redis.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.post.KafkaPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheServiceImpl implements PostCacheService {

    private final RedisPostRepository redisPostRepository;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserRepository redisUserRepository;
    private final RedisUserMapper redisUserMapper;
    private final RedisFeedRepository redisFeedRepository;
    private final UserServiceClient userServiceClient;
    private final RedisTemplate redisTemplate;
    private final CommentRepository commentRepository;

    @Value("${cache.max-comments}")
    private Integer maxCommentsInCache;
    @Value("${feed.feed-size}")
    private Integer postsFeedSize;

    @Override
    public void putPostAndAuthorInCache(PostDto post) {
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void addPostInFeed(KafkaPostDto kafkaPostDto) {
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void deletePostFromFeed(KafkaPostDto kafkaPostDto) {
    }

    @Override
    public void deletePostFromCache(PostDto post) {
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void updatePostInCache(PostDto post) {
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void addPostView(PostDto postDto) {
        redisPostRepository.findById(postDto.getId()).ifPresent(redisPost -> {
            redisPost.viewIncrement();
            updateRedisPost(redisPost);
        });
    }

    private void updateRedisPost(Object redisPost) {
        redisTemplate.opsForValue().set(String.valueOf(redisPost.hashCode()), redisPost);
    }
}