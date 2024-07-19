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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

    public RedisComment saveCommentToRedis(Long commentId) {
        CommentDto dto = commentService.getById(commentId);
        RedisComment newComment = RedisComment.builder()
                .id(commentId)
                .likesIds(new TreeSet<>())
                .version(1L)
                .ttl(commentTtl)
                .build();
        redisCommentRepository.save(newComment.getId(), newComment);
        return newComment;
    }

    public void saveNewCommentToRedis(CommentDto dto) {
        RedisComment newComment = RedisComment.builder()
                .id(dto.getId())
                .likesIds(new TreeSet<>())
                .version(1L)
                .ttl(commentTtl)
                .build();
        redisCommentRepository.save(newComment.getId(), newComment);
    }

    public RedisPost savePostToRedis(Long postId) {
        PostDto postDto = postService.getById(postId);
        TreeSet<Long> commentIds = commentService.getAllPostComments(postId).stream()
                .map(CommentDto::getId).collect(Collectors.toCollection(TreeSet::new));

        RedisPost redisPost = RedisPost.builder()
                .id(postId)
                .postDto(postDto)
                .redisCommentsIds(commentIds)
                .viewerIds(postDto.getViewersIds())
                .version(1L)
                .ttl(postTtl)
                .build();
        redisPostRepository.save(redisPost.getId(), redisPost);
        return redisPost;
    }

    public void saveNewPostToRedis(PostDto postDto) {
        RedisPost redisPost = RedisPost.builder()
                .id(postDto.getId())
                .postDto(postDto)
                .redisCommentsIds(new TreeSet<>())
                .viewerIds(new HashSet<>())
                .version(1L)
                .ttl(postTtl)
                .build();
        redisPostRepository.save(redisPost.getId(), redisPost);
    }

    public void saveUserToRedis(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);

        HashSet<Long> followingsIds = userServiceClient.getFollowings(userId).stream()
                .map(UserDto::getId).collect(Collectors.toCollection(HashSet::new));

        HashSet<Long> followersIds = userServiceClient.getFollowers(userId).stream()
                .map(UserDto::getId).collect(Collectors.toCollection(HashSet::new));

        RedisUser redisUser = RedisUser.builder()
                .id(userId)
                .userDto(userDto)
                .followingsIds(followingsIds)
                .followersIds(followersIds)
                .version(1L)
                .ttl(userTtl)
                .build();

        redisUserRepository.save(redisUser.getId(), redisUser);
    }

    public RedisFeed buildAndSaveNewRedisFeed(Long userId) {
        TreeSet<Long> postsIds = postService.findUserFollowingsPosts(userId, LocalDateTime.now(), 500).stream()
                .map(PostDto::getId)
                .collect(Collectors.toCollection(TreeSet::new));

        RedisFeed redisFeed = RedisFeed.builder()
                .userId(userId)
                .postsIds(postsIds)
                .version(1L)
                .ttl(feedTtl)
                .build();

        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
        return redisFeed;
    }

}