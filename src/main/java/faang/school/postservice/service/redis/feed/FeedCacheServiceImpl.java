package faang.school.postservice.service.redis.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.entity.model.redis.RedisFeed;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.event.post.DeletePostEvent;
import faang.school.postservice.event.post.NewPostEvent;
import faang.school.postservice.event.post.PostViewEvent;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheServiceImpl implements FeedCacheService {

    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentService commentService;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserMapper redisUserMapper;

    @Value("${spring.data.redis.ttl.post}")
    private Long postTtl;

    @Value("${spring.data.redis.ttl.user}")
    private Long userTtl;

    @Value("${spring.data.redis.ttl.feed}")
    private Long feedTtl;

    @Value("${batch.feed.size}")
    private Integer postsFeedSize;

    @Value("${batch.feed.posts-fetch}")
    private Integer fetchPostsBatch;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public LinkedHashSet<RedisPost> getNewsFeed(Long userId, Long lastPostId) {
        RedisFeed redisFeed = redisFeedRepository.getById(userId);

        if (redisFeed == null) {
            redisFeed = buildAndSaveNewRedisFeed(userId);
        }

        LinkedHashSet<RedisPost> posts = getPostsBatch(lastPostId, redisFeed);
        validatePostsBatchSize(userId, posts, redisFeed);

        log.info("Got news feed for user {}", userId);
        return posts;
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addPostInFeed(NewPostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getId());

        RedisUser redisUser = redisUserRepository.getById(event.getAuthorId());

        if (redisPost == null) {
            redisPost = savePostToRedis(event.getId());
        }

        if (redisUser == null) {
            saveUserToRedis(event.getAuthorId());
        }

        addPostToUserFeed(event.getAuthorId(), redisPost);
        addPostToFollowers(event.getFollowersIds(), redisPost);
        log.info("Added post {} to user {} feed and their followers' feeds",
                redisPost.getId(), event.getAuthorId());
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deletePostFromFeed(DeletePostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostId());

        if (redisPost != null) {
            RedisPost curentRedisPost = redisPostRepository.getById(event.getPostId());

            if (curentRedisPost != null && !curentRedisPost.getVersion().equals(redisPost.getVersion())) {
                throw new OptimisticLockingFailureException("Version conflict: current post version "
                        + curentRedisPost.getVersion() + " does not match " + redisPost.getVersion());
            }
            redisPost.incrementVersion();

            removePostFromUserFeed(event.getAuthorId(), redisPost);
            removePostFromFollowers(event.getFollowersIds(), redisPost);
            log.info("Removed post {} from user {} feed and their followers' feeds",
                    redisPost.getId(), event.getAuthorId());
        }
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addPostView(PostViewEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostId());

        if (redisPost == null) {
            redisPost = savePostToRedis(event.getPostId());
        }

        if(!redisPost.getViewersIds().contains(event.getViewerId())) {
            redisPost.getViewersIds().add(event.getViewerId());
            redisPostRepository.save(redisPost.getId(), redisPost);
            log.info("Added user/project {} view to post {}", event.getViewerId(), event.getPostId());
        }
    }

    @Async("postRemoveOrAddExecutor")
    public void addPostToFollowers(Set<Long> followersIds, RedisPost redisPost) {
        followersIds.forEach(followerId -> addPostToUserFeed(followerId, redisPost));
    }

    @Async("postRemoveOrAddExecutor")
    public void removePostFromFollowers(Set<Long> followersIds, RedisPost redisPost) {
        followersIds.forEach(followerId -> removePostFromUserFeed(followerId, redisPost));
    }

    private LinkedHashSet<RedisPost> getPostsBatch(Long lastPostId, RedisFeed redisFeed) {
        LinkedHashSet<Long> redisPostIds = redisFeed.getPostsIds();
        LinkedHashSet<RedisPost> posts = new LinkedHashSet<>();

        boolean startAdding = (lastPostId == null);
        for (Long postId : redisPostIds) {
            if (!startAdding) {
                if (postId.equals(lastPostId)) {
                    startAdding = true;
                }
                continue;
            }

            if (posts.size() >= fetchPostsBatch) break;

            RedisPost redisPost = redisPostRepository.getById(postId);

            if (redisPost == null) {
                redisPost = savePostToRedis(postId);
            }
            posts.add(redisPost);
        }
        return posts;
    }

    @Retryable(retryFor = { OptimisticLockingFailureException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    private void validatePostsBatchSize(Long userId, LinkedHashSet<RedisPost> posts, RedisFeed redisFeed) {
        if (posts.size() < fetchPostsBatch) {
            RedisFeed curentRedisFeed = redisFeedRepository.getById(userId);
            if (curentRedisFeed != null && !curentRedisFeed.getVersion().equals(redisFeed.getVersion())) {
                throw new OptimisticLockingFailureException("Version conflict: current feed version "
                        + curentRedisFeed.getVersion() + " does not match " + redisFeed.getVersion());
            }
            redisFeed.incrementVersion();

            LocalDateTime newLastPostDate = posts.isEmpty() ? LocalDateTime.now() : posts
                    .stream().max(Comparator.comparing(RedisPost::getPublishedAt)).get().getPublishedAt();

            List<PostDto> fetchedPosts = fetchAdditionalPostsFromDb(userId, newLastPostDate, postsFeedSize);

            Set<RedisPost> redisPostsToAdd = fetchedPosts.stream()
                    .map(postDto -> savePostToRedis(postDto.getId()))
                    .collect(Collectors.toSet());

            posts.addAll(redisPostsToAdd);
            redisFeed.getPostsIds().addAll(fetchedPosts.stream().map(PostDto::getId).collect(Collectors.toSet()));
            redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
        }
    }

    private List<PostDto> fetchAdditionalPostsFromDb(Long userId, LocalDateTime date, int limit) {
        return postService.findUserFollowingsPosts(userId, date, limit);
    }

    private void addPostToUserFeed(Long userId, RedisPost redisPost) {
        RedisFeed redisFeed = redisFeedRepository.getById(userId);

        if (redisFeed == null) {
            redisFeed = buildAndSaveNewRedisFeed(userId);
        }

        if (redisFeed.getPostsIds().size() >= postsFeedSize) {
            redisFeed.getPostsIds().remove(redisFeed.getPostsIds().iterator().next());
        }

        redisFeed.getPostsIds().add(redisPost.getId());
        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
    }

    private void removePostFromUserFeed(Long userId, RedisPost redisPost) {
        RedisFeed redisFeed = redisFeedRepository.getById(userId);

        if (redisFeed == null) {
            redisFeed = buildAndSaveNewRedisFeed(userId);
        }

        redisFeed.getPostsIds().remove(redisPost.getId());
        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
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

    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
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

    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
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
