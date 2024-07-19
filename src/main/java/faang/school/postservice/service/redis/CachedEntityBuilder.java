package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.entity.model.redis.*;
import faang.school.postservice.repository.redis.*;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.TreeSet;

@Component
@RequiredArgsConstructor
public class CachedEntityBuilder {

    private final UserServiceClient userServiceClient;
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisCommentRepository redisCommentRepository;
    private final RedisUserRepository redisUserRepository;
    private final CommentService commentService;
    private final PostService postService;

    @Value("${spring.data.redis.ttl.post}")
    private Long postTtl;

    @Value("${spring.data.redis.ttl.user}")
    private Long userTtl;

    @Value("${spring.data.redis.ttl.comment}")
    private Long commentTtl;

    @Value("${spring.data.redis.ttl.feed}")
    private Long feedTtl;

    public RedisComment buildAndSaveNewRedisComment(Long commentId) {
        CommentDto dto = commentService.getById(commentId);
        RedisComment newComment = RedisComment.builder()
                .id(commentId)
                .commentDto(dto)
                .redisLikesIds(new TreeSet<>())
                .version(1L)
                .ttl(commentTtl)
                .build();
        redisCommentRepository.save(newComment.getId(), newComment);
        return newComment;
    }

    public RedisPost buildAndSaveNewRedisPost(Long postId) {
        PostDto postDto = postService.getById(postId);
        RedisPost redisPost = RedisPost.builder()
                .id(postId)
                .postDto(postDto)
                .redisCommentsIds(new TreeSet<>())
                .viewerIds(new TreeSet<>())
                .version(1L)
                .ttl(postTtl)
                .build();
        redisPostRepository.save(redisPost.getId(), redisPost);
        return redisPost;
    }

    public void buildAndSaveNewRedisUser(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        RedisUser redisUser = RedisUser.builder()
                .id(userId)
                .userDto(userDto)
                .version(1L)
                .ttl(userTtl)
                .build();
        redisUserRepository.save(redisUser.getId(), redisUser);
    }

    public RedisFeed buildAndSaveNewRedisFeed(Long userId) {
        RedisFeed redisFeed = RedisFeed.builder()
                .userId(userId)
                .redisPostsIds(new TreeSet<>())
                .version(1L)
                .ttl(feedTtl)
                .build();
        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
        return redisFeed;
    }
}