package faang.school.postservice.service.redis.comment;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.SortedSet;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCacheServiceImpl implements CommentCacheService {

    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisCommentRepository redisCommentRepository;

    @Value("${cache.max-comments}")
    private Integer maxCommentsInCache;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addCommentToPost(CommentDto dto) {
        RedisPost redisPost = redisPostRepository.getById(dto.getPostId());
        RedisUser author = redisUserRepository.getById(dto.getAuthorId());

        if (redisPost == null || author == null) {
            log.error("Post or Author not found for given IDs.");
            return;
        }

        SortedSet<Long> comments = redisPost.getRedisCommentsIds();
        if (comments == null) {
            comments = new TreeSet<>();
        }

        if (comments.size() >= maxCommentsInCache) {
            comments.remove(comments.first());
        }

        RedisComment redisComment = RedisComment.builder()
                .id(dto.getId())
                .commentDto(dto)
                .version(1L)
                .build();

        comments.add(redisComment.getId());
        redisPost.setRedisCommentsIds(comments);

        redisCommentRepository.save(redisComment.getId(), redisComment);
        redisPostRepository.save(redisPost.getId(), redisPost);
        redisUserRepository.save(author.getId(), author);
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void updateCommentOnPost(CommentDto dto) {
        RedisComment redisComment = redisCommentRepository.getById(dto.getId());
        RedisUser author = redisUserRepository.getById(dto.getAuthorId());

        if (redisComment == null || author == null) {
            log.error("Comment or Author not found for given IDs.");
            return;
        }

        redisComment.setCommentDto(dto);
        redisComment.incrementVersion();

        redisCommentRepository.update(redisComment.getId(), redisComment);
        redisUserRepository.save(author.getId(), author);
    }

    @Override
    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteCommentFromPost(CommentDto dto) {
        RedisPost redisPost = redisPostRepository.getById(dto.getPostId());
        RedisComment redisComment = redisCommentRepository.getById(dto.getId());

        if (redisPost == null || redisComment == null) {
            log.error("Post or Comment not found for given IDs.");
            return;
        }

        SortedSet<Long> comments = redisPost.getRedisCommentsIds();

        if (comments != null && comments.contains(dto.getId())) {
            comments.remove(dto.getId());
            redisPost.setRedisCommentsIds(comments);
            redisPostRepository.save(redisPost.getId(), redisPost);
        }

        redisCommentRepository.remove(redisComment.getId());
    }
}