package faang.school.postservice.service.redis.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.redis.comment.RedisCommentDto;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCacheServiceImpl implements CommentCacheService {

    private final RedisUserRepository redisUserRepository;
    private final RedisUserMapper redisUserMapper;
    private final RedisCommentMapper redisCommentMapper;
    private final UserServiceClient userServiceClient;
    private final RedisTemplate<Long, RedisCommentDto> redisTemplate;
    private final RedisPostRepository redisPostRepository;

    @Value("${cache.max-comments}")
    private Integer maxCommentsInCache;

    @Override
    @Retryable(
            value = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void addCommentToPost(CommentDto comment) {
        redisPostRepository.findById(comment.getPostId()).ifPresent(redisPost -> {
            loadUserIntoCache(comment.getAuthorId());
            List<RedisCommentDto> comments = getCommentsList(redisPost);

            if (comments.size() >= maxCommentsInCache) {
                comments.remove(0);
            }

            RedisCommentDto dto = redisCommentMapper.toRedisDto(comment);
            comments.add(dto);
            redisPost.setRedisCommentDtos(comments);
            redisTemplate.opsForSet().add(redisPost.getId(), dto);
        });
    }

    @Override
    @Retryable(
            value = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void updateCommentOnPost(CommentDto comment) {
        redisPostRepository.findById(comment.getPostId()).ifPresent(redisPost -> {
            List<RedisCommentDto> comments = getCommentsList(redisPost);

            comments.replaceAll(existingComment ->
                    existingComment.getId() == comment.getId() ?
                            redisCommentMapper.toRedisDto(comment) :
                            existingComment
            );

            redisPost.setRedisCommentDtos(comments);
            redisTemplate.opsForSet().add(redisPost.getId(), redisCommentMapper.toRedisDto(comment));
        });
    }

    @Override
    @Transactional
    @Retryable(
            value = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void deleteCommentFromPost(CommentDto commentDto) {
        redisPostRepository.findById(commentDto.getPostId()).ifPresent(redisPost -> {
            List<RedisCommentDto> comments = getCommentsList(redisPost);

            comments.removeIf(comment -> comment.getId() == commentDto.getId());
            redisPost.setRedisCommentDtos(comments);
            redisTemplate.opsForSet().add(redisPost.getId(), redisCommentMapper.toRedisDto(commentDto));
        });
    }

    @Override
    public List<RedisCommentDto> getCachedComments(List<CommentDto> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }
        return comments.stream()
                .skip(Math.max(0, comments.size() - maxCommentsInCache))
                .map(redisCommentMapper::toRedisDto)
                .toList();
    }

    private void loadUserIntoCache(long userId) {
        redisUserRepository.findById(userId).orElseGet(() -> {
            RedisUser user = redisUserMapper.toEntity(userServiceClient.getUser(userId));
            return redisUserRepository.save(user);
        });
    }

    private List<RedisCommentDto> getCommentsList(RedisPost redisPost) {
        List<RedisCommentDto> comments = redisPost.getRedisCommentDtos();
        return comments != null ? comments : new ArrayList<>();
    }
}
