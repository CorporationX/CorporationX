package faang.school.postservice.service.redis.feed;

import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisFeed;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.event.post.DeletePostEvent;
import faang.school.postservice.event.post.NewPostEvent;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.validator.redis.RedisFeedValidator;
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
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheServiceImpl implements FeedCacheService {

    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;

    @Value("${feed.feed-size}")
    private Integer postsFeedSize;

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public SortedSet<PostDto> getNewsFeed(Long userId) {
        RedisFeed redisFeed = redisFeedRepository.getById(userId);

        SortedSet<Long> redisPostIds = redisFeed.getRedisPostsIds();
        SortedSet<PostDto> posts = new TreeSet<>(Comparator.comparing(PostDto::getId));

        redisPostIds.forEach(postId -> {
            RedisPost redisPost = redisPostRepository.getById(postId);
            if (redisPost != null) {
                posts.add(redisPost.getPostDto());
            }
        });

        return posts;
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addPostInFeed(NewPostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostDto().getId());
        RedisUser redisUser = redisUserRepository.getById(event.getPostDto().getAuthorId(), event.getFollowersIds());

        redisUserRepository.save(redisUser.getId(), redisUser);

        addPostToUserFeed(event.getPostDto().getAuthorId(), redisPost);
        addPostToFollowers(event.getFollowersIds(), redisPost);
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deletePostFromFeed(DeletePostEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostDto().getId());

        if (redisPost != null) {
            redisPost.incrementVersion();
            removePostFromUserFeed(event.getPostDto().getAuthorId(), redisPost);
            removePostFromFollowers(event.getFollowersIds(), redisPost);
        }
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addPostView(PostViewEvent event) {
        RedisPost redisPost = redisPostRepository.getById(event.getPostId());

        if (redisPost != null) {
            redisPost.getPostDto().incrementViews();
            redisPost.incrementVersion();
            redisPostRepository.save(redisPost.getId(), redisPost);
        }
    }

    @Async("postRemoveOrAddExecutor")
    protected CompletableFuture<Void> addPostToFollowers(SortedSet<Long> followersIds, RedisPost redisPost) {
        followersIds.forEach(followerId -> addPostToUserFeed(followerId, redisPost));
        return CompletableFuture.completedFuture(null);
    }

    @Async("postRemoveOrAddExecutor")
    protected CompletableFuture<Void> removePostFromFollowers(SortedSet<Long> followersIds, RedisPost redisPost) {
        followersIds.forEach(followerId -> removePostFromUserFeed(followerId, redisPost));
        return CompletableFuture.completedFuture(null);
    }

    private void addPostToUserFeed(Long userId, RedisPost redisPost) {
        RedisFeed followerFeed = redisFeedRepository.getById(userId);

        if (followerFeed.getRedisPostsIds().size() >= postsFeedSize) {
            followerFeed.incrementVersion();
            followerFeed.getRedisPostsIds().remove(followerFeed.getRedisPostsIds().last());
        }
        followerFeed.getRedisPostsIds().add(redisPost.getId());
        followerFeed.incrementVersion();
        redisFeedRepository.save(followerFeed.getUserId(), followerFeed);
    }

    private void removePostFromUserFeed(Long userId, RedisPost redisPost) {
        RedisFeed followerFeed = redisFeedRepository.getById(userId);
        if (followerFeed != null) {
            followerFeed.getRedisPostsIds().remove(redisPost.getId());
            followerFeed.incrementVersion();
            redisFeedRepository.save(followerFeed.getUserId(), followerFeed);
        }
    }
}