package faang.school.postservice.service.redis.comment;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.comment.DeleteCommentEvent;
import faang.school.postservice.event.comment.NewCommentEvent;
import faang.school.postservice.mapper.redis.RedisPostMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCacheServiceImpl implements CommentCacheService {

    private final RedisPostRepository redisPostRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final RedisPostMapper redisPostMapper;

    @Value("${spring.data.redis.ttl.post}")
    private Long postTtl;

    @Value("${cache.max-comments}")
    private Integer maxCommentsInCache;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addCommentToPost(NewCommentEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostId());

        if (redisPost == null) {
            redisPost = savePostToRedis(event.getPostId());
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

    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public RedisPost savePostToRedis(Long postId) {
        PostDto postDto = postService.getById(postId);
        LinkedHashSet<Long> commentIds = commentService.getAllPostComments(postId).stream()
                .map(CommentDto::getId).collect(Collectors.toCollection(LinkedHashSet::new));

        RedisPost redisPost = redisPostMapper.toRedisPost(postDto);
        redisPost.setLikes(0L);
        redisPost.setCommentsIds(commentIds);
        redisPost.setViewersIds(new HashSet<>());
        redisPost.setVersion(1L);
        redisPost.setTtl(postTtl);
        redisPostRepository.save(redisPost.getId(), redisPost);
        return redisPost;
    }
}
