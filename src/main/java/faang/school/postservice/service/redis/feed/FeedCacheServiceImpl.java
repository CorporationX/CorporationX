package faang.school.postservice.service.redis.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.entity.model.redis.RedisFeed;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.event.post.PostViewEvent;
import faang.school.postservice.event.post.DeletePostEvent;
import faang.school.postservice.event.post.NewPostEvent;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheServiceImpl implements FeedCacheService {

    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;

    @Value("${feed.feed-size}")
    private Integer postsFeedSize;

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public SortedSet<PostDto> getNewsFeed(Long userId) {
        RedisFeed redisFeed = redisFeedRepository.getById(userId);
        if (redisFeed == null) {
            redisFeed = buildAndSaveNewRedisFeed(userId);
        }
        SortedSet<Long> redisPostIds = redisFeed.getRedisPostsIds();
        SortedSet<PostDto> posts = new TreeSet<>(Comparator.comparing(PostDto::getId));
        redisPostIds.forEach(postId -> {
            RedisPost redisPost = redisPostRepository.getById(postId);
            if (redisPost != null) {
                posts.add(redisPost.getPostDto());
            }
        });

        log.info("Got news feed for user {}", userId);
        return posts;
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addPostInFeed(NewPostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostDto().getId());
        RedisUser redisUser = redisUserRepository.getById(event.getPostDto().getAuthorId());

        if (redisPost == null) {
            redisPost = buildAndSaveNewRedisPost(event.getPostDto().getId());
        }

        if (redisUser == null) {
            buildAndSaveNewRedisUser(event.getPostDto().getAuthorId());
        }

        addPostToUserFeed(event.getPostDto().getAuthorId(), redisPost);
        addPostToFollowers(event.getFollowersIds(), redisPost);
        log.info("Added post {} to user {} feed and their followers feeds", redisPost.getId(),
                redisPost.getPostDto().getAuthorId());
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deletePostFromFeed(DeletePostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostDto().getId());

        if (redisPost != null) {
            redisPost.incrementVersion();

            removePostFromUserFeed(event.getPostDto().getAuthorId(), redisPost);
            removePostFromFollowers(event.getFollowersIds(), redisPost);
            log.info("Removed post {} from user {} feed and their followers feeds", redisPost.getId(),
                    redisPost.getPostDto().getAuthorId());
        }
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addPostView(PostViewEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostDto().getId());

        if (redisPost == null) {
            redisPost = buildAndSaveNewRedisPost(event.getPostDto().getId());
        }

        redisPost.getPostDto().incrementViews();
        redisPost.incrementVersion();
        redisPostRepository.save(redisPost.getId(), redisPost);
        log.info("Added view to post {}", redisPost.getId());
    }

    @Async("postRemoveOrAddExecutor")
    public void addPostToFollowers(SortedSet<Long> followersIds, RedisPost redisPost) {
        followersIds.forEach(followerId -> addPostToUserFeed(followerId, redisPost));
    }

    @Async("postRemoveOrAddExecutor")
    public void removePostFromFollowers(SortedSet<Long> followersIds, RedisPost redisPost) {
        followersIds.forEach(followerId -> removePostFromUserFeed(followerId, redisPost));
    }

    private void addPostToUserFeed(Long userId, RedisPost redisPost) {
        RedisFeed redisFeed = redisFeedRepository.getById(userId);

        if (redisFeed == null) {
            redisFeed = buildAndSaveNewRedisFeed(userId);
        }

        if (redisFeed.getRedisPostsIds().size() >= postsFeedSize) {
            redisFeed.incrementVersion();
            redisFeed.getRedisPostsIds().remove(redisFeed.getRedisPostsIds().last());
        }

        redisFeed.getRedisPostsIds().add(redisPost.getId());
        redisFeed.incrementVersion();
        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
    }

    private void removePostFromUserFeed(Long userId, RedisPost redisPost) {
        RedisFeed redisFeed = redisFeedRepository.getById(userId);
        if (redisFeed != null) {
            redisFeed.getRedisPostsIds().remove(redisPost.getId());
            redisFeed.incrementVersion();
            redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
        }
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

    private void buildAndSaveNewRedisUser(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        RedisUser.builder()
                .id(userId)
                .userDto(userDto)
                .version(1L)
                .build();
    }

    private RedisFeed buildAndSaveNewRedisFeed(Long userId) {
        RedisFeed redisFeed = RedisFeed.builder()
                .userId(userId)
                .redisPostsIds(new TreeSet<>())
                .version(1L)
                .build();
        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
        return redisFeed;
    }
}