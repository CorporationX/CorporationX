package faang.school.postservice.service.redis.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.redis.comment.RedisCommentDto;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCacheServiceImpl implements CommentCacheService {

    private final RedisUserRepository redisUserRepository;
    private final RedisUserMapper redisUserMapper;
    private final RedisFeedRepository redisFeedRepository;
    private final RedisCommentMapper redisCommentMapper;
    private final UserServiceClient userServiceClient;
    private final RedisKeyValueTemplate redissTemplate;
    private final CommentRepository commentRepository;
    private final RedisPostRepository redisPostRepository;

    @Value("${cache.max-comments}")
    private Integer maxCommentsInCache;
    @Value("${feed.feed-size}")
    private Integer postsFeedSize;


    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void addCommentToPost(CommentDto comment) {
        redisPostRepository.findById(comment.getPostId()).ifPresent(redisPost -> {
            getUserFromCache(comment.getAuthorId());
            List<RedisCommentDto> comments = redisPost.getRedisCommentDtos();

            if (comments == null) {
                comments = new ArrayList<>();
            }
            if (comments.size() >= maxCommentsInCache) {
                comments.remove(0);
            }

            comments.add(redisCommentMapper.toRedisDto(comment));
            redisPost.setRedisCommentDtos(comments);
            redisTemplate.update(redisPost);
        });
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void updateCommentOnPost(CommentDto comment) {
    }

    @Override
    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void deleteCommentFromPost(CommentDto commentDto) {
    }

    @Override
    public List<RedisCommentDto> getCachedComments(List<CommentDto> comments) {
        return null;
    }

    private void getUserFromCache(long userId) {
        redisUserRepository.findById(userId).orElseGet(() -> {
            RedisUser user = redisUserMapper.toEntity(userServiceClient.getUser(userId));
            return redisUserRepository.save(user);
        });
    }

    public RedisKeyValueTemplate getRedissTemplate() {
        return redissTemplate;
    }
}