package faang.school.postservice.service.redis.like;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.like.DeleteCommentLikeEvent;
import faang.school.postservice.event.like.DeletePostLikeEvent;
import faang.school.postservice.event.like.LikeCommentEvent;
import faang.school.postservice.event.like.LikePostEvent;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeCacheServiceImpl implements LikeCacheService {

    private final RedisPostRepository redisPostRepository;
    private final RedisCommentRepository redisCommentRepository;
    private final CommentService commentService;
    private final PostService postService;
    private final RedisCommentMapper redisCommentMapper;
    private final RedisPostMapper redisPostMapper;

    @Value("${spring.data.redis.ttl.post}")
    private Long postTtl;

    @Value("${spring.data.redis.ttl.comment}")
    private Long commentTtl;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addLikeOnPost(LikePostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostId());

        if (redisPost == null) {
            redisPost = savePostToRedis(event.getPostId());
        }

        redisPost.incrementLikes();
        redisPostRepository.save(redisPost.getId(), redisPost);

        log.info("Added like to post {}", event.getPostId());
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromPost(DeletePostLikeEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostId());

        if (redisPost != null) {
            redisPost.decrementLikes();
            redisPostRepository.save(redisPost.getId(), redisPost);
            log.info("Deleted like from post {}", event.getPostId());
        }
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addLikeToComment(LikeCommentEvent event) {
        RedisComment redisComment = redisCommentRepository.getById(event.getCommentId());

        if (redisComment == null) {
            redisComment = saveCommentToRedis(event.getCommentId());
        }

        redisComment.decrementLikes();
        redisCommentRepository.save(redisComment.getId(), redisComment);

        log.info("Added like to comment {}", event.getCommentId());
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromComment(DeleteCommentLikeEvent event) {
        RedisComment redisComment = redisCommentRepository.getById(event.getCommentId());

        if (redisComment != null) {
            redisComment.decrementLikes();
            redisCommentRepository.save(redisComment.getId(), redisComment);
            log.info("Deleted like from comment {}", event.getCommentId());
        }
    }

    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public RedisComment saveCommentToRedis(Long commentId) {
        CommentDto dto = commentService.getById(commentId);
        RedisComment newComment = redisCommentMapper.toRedisDto(dto);
        newComment.setVersion(1L);
        newComment.setTtl(commentTtl);
        redisCommentRepository.save(newComment.getId(), newComment);
        return newComment;
    }

    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public RedisPost savePostToRedis(Long postId) {
        LinkedHashSet<Long> commentIds = commentService.getAllPostComments(postId).stream()
                .map(CommentDto::getId).collect(Collectors.toCollection(LinkedHashSet::new));

        RedisPost redisPost = redisPostMapper.toRedisPost(postService.getById(postId));
        redisPost.setCommentsIds(commentIds);
        redisPost.setVersion(1L);
        redisPost.setTtl(postTtl);
        redisPostRepository.save(redisPost.getId(), redisPost);
        return redisPost;
    }
}