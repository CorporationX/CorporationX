package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisFeed;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
    private final RedisCommentMapper redisCommentMapper;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserMapper redisUserMapper;

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
        RedisComment newComment = redisCommentMapper.toRedisDto(dto);
        newComment.setVersion(1L);
        newComment.setTtl(commentTtl);
        redisCommentRepository.save(newComment.getId(), newComment);
        return newComment;
    }

    public void saveNewCommentToRedis(CommentDto dto) {
        RedisComment newComment = redisCommentMapper.toRedisDto(dto);
        newComment.setVersion(1L);
        newComment.setTtl(commentTtl);
        redisCommentRepository.save(newComment.getId(), newComment);
    }

    public RedisPost savePostToRedis(Long postId) {
        PostDto postDto = postService.getById(postId);
        LinkedHashSet<Long> commentIds = commentService.getAllPostComments(postId).stream()
                .map(CommentDto::getId).collect(Collectors.toCollection(LinkedHashSet::new));

        RedisPost redisPost = redisPostMapper.toRedisPost(postDto);
        redisPost.setCommentsIds(commentIds);
        redisPost.setVersion(1L);
        redisPost.setTtl(postTtl);
        redisPostRepository.save(redisPost.getId(), redisPost);
        return redisPost;
    }

    public void saveNewPostToRedis(PostDto postDto) {
        RedisPost redisPost = redisPostMapper.toRedisPost(postDto);
        redisPost.setCommentsIds(new LinkedHashSet<>());
        redisPost.setVersion(1L);
        redisPost.setTtl(postTtl);
        redisPostRepository.save(redisPost.getId(), redisPost);
    }

    public void saveUserToRedis(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);

        HashSet<Long> followingsIds = userServiceClient.getFollowings(userId).stream()
                .map(UserDto::getId).collect(Collectors.toCollection(HashSet::new));

        HashSet<Long> followersIds = userServiceClient.getFollowers(userId).stream()
                .map(UserDto::getId).collect(Collectors.toCollection(HashSet::new));

        RedisUser redisUser = redisUserMapper.toRedisDto(userDto);

        redisUser.setFollowingsIds(followingsIds);
        redisUser.setFollowersIds(followersIds);
        redisUser.setVersion(1L);
        redisUser.setTtl(userTtl);

        redisUserRepository.save(redisUser.getId(), redisUser);
    }

    public RedisFeed buildAndSaveNewRedisFeed(Long userId) {
        LinkedHashSet<Long> postsIds = postService.findUserFollowingsPosts(userId, LocalDateTime.now(), 500).stream()
                .map(PostDto::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

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