package faang.school.postservice.service.redis.like;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.like.DeleteCommentLikeEvent;
import faang.school.postservice.event.like.DeletePostLikeEvent;
import faang.school.postservice.event.like.LikeCommentEvent;
import faang.school.postservice.event.like.LikePostEvent;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeCacheServiceImpl implements LikeCacheService {

    private final RedisPostRepository redisPostRepository;
    private final RedisCommentRepository redisCommentRepository;
    private final PostService postService;
    private final CommentService commentService;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addLikeOnPost(LikePostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getLikeDto().getPostId());
        if (redisPost == null) {
            redisPost = buildAndSaveNewRedisPost(event.getLikeDto().getPostId());
        }
        redisPost.getPostDto().getLikesIds().add(event.getLikeDto().getId());
        redisPostRepository.save(redisPost.getId(), redisPost);
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromPost(DeletePostLikeEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getLikeDto().getPostId());
        if (redisPost == null) {
            redisPost = buildAndSaveNewRedisPost(event.getLikeDto().getPostId());
        }
        redisPost.getPostDto().getLikesIds().remove(event.getLikeDto().getId());
        redisPostRepository.save(redisPost.getId(), redisPost);
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addLikeToComment(LikeCommentEvent event) {
        RedisComment redisComment = redisCommentRepository.getById(event.getLikeDto().getCommentId());
        if (redisComment == null) {
            redisComment = buildAndSaveNewRedisComment(event.getLikeDto().getCommentId());
        }
        redisComment.getRedisLikesIds().add(event.getLikeDto().getId());
        redisCommentRepository.save(redisComment.getId(), redisComment);
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromComment(DeleteCommentLikeEvent event) {
        RedisComment redisComment = redisCommentRepository.getById(event.getLikeDto().getCommentId());
        if (redisComment == null) {
            redisComment = buildAndSaveNewRedisComment(event.getLikeDto().getCommentId());
        }
        redisComment.getRedisLikesIds().remove(event.getLikeDto().getId());
        redisCommentRepository.save(redisComment.getId(), redisComment);
    }

    private RedisComment buildAndSaveNewRedisComment(Long commentId) {
        CommentDto dto = commentService.getById(commentId);
        RedisComment newComment = RedisComment.builder()
                .id(commentId)
                .commentDto(dto)
                .redisLikesIds(new TreeSet<>())
                .version(1L)
                .build();
        redisCommentRepository.save(newComment.getId(), newComment);
        return newComment;
    }

    private RedisPost buildAndSaveNewRedisPost(Long postId) {
        PostDto postDto = postService.getById(postId);
        RedisPost redisPost = RedisPost.builder()
                .id(postId)
                .postDto(postDto)
                .redisCommentsIds(new TreeSet<>())
                .viewerIds(new TreeSet<>())
                .version(1L)
                .build();
        redisPostRepository.save(redisPost.getId(), redisPost);
        return redisPost;
    }
}
