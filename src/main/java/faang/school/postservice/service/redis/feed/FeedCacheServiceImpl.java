package faang.school.postservice.service.redis.feed;

import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisFeed;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.event.post.DeletePostEvent;
import faang.school.postservice.event.post.NewPostEvent;
import faang.school.postservice.event.post.PostViewEvent;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.redis.CachedEntityBuilder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

    @Setter
    private CachedEntityBuilder cachedEntity;

    @Value("${batch.feed.size}")
    private Integer postsFeedSize;

    @Value("${batch.feed.posts-fetch}")
    private Integer fetchPostsBatch;

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public LinkedHashSet<RedisPost> getNewsFeed(Long userId, Long lastPostId) {
        RedisFeed redisFeed = redisFeedRepository.getById(userId);

        if (redisFeed == null) {
            redisFeed = cachedEntity.buildAndSaveNewRedisFeed(userId);
        }

        LinkedHashSet<RedisPost> posts = getPostsBatch(lastPostId, redisFeed);
        validatePostsBatchSize(userId, posts, redisFeed);

        log.info("Got news feed for user {}", userId);
        return posts;
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addPostInFeed(NewPostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostDto().getId());
        RedisUser redisUser = redisUserRepository.getById(event.getPostDto().getAuthorId());

        if (redisPost == null) {
            redisPost = cachedEntity.savePostToRedis(event.getPostDto().getId());
        }

        if (redisUser == null) {
            cachedEntity.saveUserToRedis(event.getPostDto().getAuthorId());
        }

        addPostToUserFeed(event.getPostDto().getAuthorId(), redisPost);
        addPostToFollowers(event.getFollowersIds(), redisPost);
        log.info("Added post {} to user {} feed and their followers' feeds",
                redisPost.getId(), event.getPostDto().getAuthorId());
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deletePostFromFeed(DeletePostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostDto().getId());

        if (redisPost != null) {
            redisPost.incrementVersion();

            removePostFromUserFeed(event.getPostDto().getAuthorId(), redisPost);
            removePostFromFollowers(event.getFollowersIds(), redisPost);
            log.info("Removed post {} from user {} feed and their followers' feeds",
                    redisPost.getId(), event.getPostDto().getAuthorId());
        }
    }

    @Override
    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addPostView(PostViewEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostDto().getId());

        if (redisPost == null) {
            redisPost = cachedEntity.savePostToRedis(event.getPostDto().getId());
        }

        redisPost.getViewersIds().add(event.getViewerId());
        redisPost.incrementVersion();
        redisPostRepository.save(redisPost.getId(), redisPost);
        log.info("Added view to post {}", redisPost.getId());
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
                redisPost = cachedEntity.savePostToRedis(postId);
            }
            posts.add(redisPost);
        }
        return posts;
    }

    private void validatePostsBatchSize(Long userId, LinkedHashSet<RedisPost> posts, RedisFeed redisFeed) {
        if (posts.size() < fetchPostsBatch) {
            LocalDateTime newLastPostDate = posts.isEmpty() ? LocalDateTime.now() : posts
                    .stream().max(Comparator.comparing(RedisPost::getPublishedAt)).get().getPublishedAt();

            List<PostDto> fetchedPosts = fetchAdditionalPostsFromDb(userId, newLastPostDate, postsFeedSize);

            Set<RedisPost> redisPostsToAdd = fetchedPosts.stream()
                    .map(postDto -> cachedEntity.savePostToRedis(postDto.getId()))
                    .collect(Collectors.toSet());

            posts.addAll(redisPostsToAdd);
            redisFeed.getPostsIds().addAll(fetchedPosts.stream().map(PostDto::getId).collect(Collectors.toSet()));
            redisFeed.incrementVersion();
            redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
        }
    }

    private List<PostDto> fetchAdditionalPostsFromDb(Long userId, LocalDateTime date, int limit) {
        return postService.findUserFollowingsPosts(userId, date, limit);
    }

    private void addPostToUserFeed(Long userId, RedisPost redisPost) {
        RedisFeed redisFeed = redisFeedRepository.getById(userId);

        if (redisFeed == null) {
            redisFeed = cachedEntity.buildAndSaveNewRedisFeed(userId);
        }

        if (redisFeed.getPostsIds().size() >= postsFeedSize) {
            redisFeed.getPostsIds().remove(redisFeed.getPostsIds().iterator().next());
        }

        redisFeed.getPostsIds().add(redisPost.getId());
        redisFeed.incrementVersion();
        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
    }

    private void removePostFromUserFeed(Long userId, RedisPost redisPost) {
        RedisFeed redisFeed = redisFeedRepository.getById(userId);

        if (redisFeed == null) {
            redisFeed = cachedEntity.buildAndSaveNewRedisFeed(userId);
        }

        redisFeed.getPostsIds().remove(redisPost.getId());
        redisFeed.incrementVersion();
        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
    }
}
