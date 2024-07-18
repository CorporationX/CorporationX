package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisFeed;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.kafka.producer.PostViewProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.hashtag.async.AsyncHashtagService;
import faang.school.postservice.service.kafka.KafkaPostService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.spelling.SpellingService;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.TreeSet;

@Component
@RequiredArgsConstructor
public class Builder {
    private final UserServiceClient userServiceClient;
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisCommentRepository redisCommentRepository;
    private final RedisUserRepository redisUserRepository;
    private final CommentService commentService;
    private final PostService postService;

    public RedisComment buildAndSaveNewRedisComment(Long commentId) {
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

    public RedisPost buildAndSaveNewRedisPost(Long postId) {
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

    public void buildAndSaveNewRedisUser(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        RedisUser redisUser = RedisUser.builder()
                .id(userId)
                .userDto(userDto)
                .version(1L)
                .build();
        redisUserRepository.save(redisUser.getId(), redisUser);
    }

    public RedisFeed buildAndSaveNewRedisFeed(Long userId) {
        RedisFeed redisFeed = RedisFeed.builder()
                .userId(userId)
                .redisPostsIds(new TreeSet<>())
                .version(1L)
                .build();
        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
        return redisFeed;
    }
}
