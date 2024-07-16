package faang.school.postservice.validator.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.entity.model.redis.RedisFeed;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.SortedSet;
import java.util.TreeSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPostValidator {

    private final RedisPostRepository redisPostRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;


    public RedisFeed validateFeedExistence(Long userId, RedisFeed redisFeed) {
        if (redisFeed == null) {
            redisFeed = buildNewRedisFeed(userId);
        }
        return redisFeed;
    }

    public RedisPost validatePostExistence(Long postId, RedisPost redisPost) {
        if (redisPost == null) {
            redisPost = buildNewRedisPost(postId);
        }
        return redisPost;
    }

    public RedisUser validateUserExistence(Long userId, RedisUser redisUser, SortedSet<Long> followersIds) {
        if (redisUser == null) {
            redisUser = buildNewRedisUser(userId, followersIds);
        }
        return redisUser;
    }

    private RedisPost buildNewRedisPost(Long postId) {
        PostDto postDto = postService.getById(postId);
        RedisPost redisPost = RedisPost.builder()
                .id(postId)
                .postDto(postDto)
                .redisCommentsIds(new TreeSet<>())
                .redisLikesIds(new TreeSet<>())
                .viewerIds(new TreeSet<>())
                .version(1L)
                .build();
        redisPostRepository.save(redisPost.getId(), redisPost);
        return redisPost;
    }

    private RedisUser buildNewRedisUser(Long userId, SortedSet<Long> followersIds) {
        UserDto userDto = userServiceClient.getUser(userId);
        return RedisUser.builder()
                .id(userId)
                .userDto(userDto)
                .followersIds(followersIds)
                .version(1L)
                .build();
    }

    private RedisFeed buildNewRedisFeed(Long userId) {
        TreeSet<Long> postIds = new TreeSet<>();

        RedisFeed redisFeed = RedisFeed.builder()
                .userId(userId)
                .redisPostsIds(postIds)
                .version(1L)
                .build();
        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
        return redisFeed;
    }
}
