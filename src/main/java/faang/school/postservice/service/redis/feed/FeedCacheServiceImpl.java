package faang.school.postservice.service.redis.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.entity.model.redis.RedisFeed;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheServiceImpl implements FeedCacheService {

    private final RedisPostRepository redisPostRepository;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserRepository redisUserRepository;
    private final RedisUserMapper redisUserMapper;
    private final PostRepository postRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final UserServiceClient userServiceClient;
    private final RedisTemplate<Long, RedisFeed> redisFeedTemplate;
    private final RedisTemplate<Long, RedisUser> redisUsersTemplate;
    private final RedisTemplate<Long, RedisPost> redisPostsTemplate;
    private final CommentRepository commentRepository;

    @Value("${cache.max-comments}")
    private Integer maxCommentsInCache;
    @Value("${feed.feed-size}")
    private Integer postsFeedSize;

    @Override
    public TreeSet<PostDto> getNewsFeed(Long userId) {
        return null;
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addPostInFeed(PostDto postDto) {
        List<UserDto> followers = userServiceClient.getFollowers(postDto.getAuthorId());
        for (UserDto follower : followers) {
            Optional<RedisFeed> optional = redisFeedRepository.findById(follower.getId());
            if (optional.isPresent()) {
                RedisFeed feed = optional.get();
                SortedSet<RedisPostId> postIds = feed.getPostIds();
                if (postIds.size() >= postsFeedSize) {
                    postIds.remove(postIds.first());
                }
                postIds.add(new RedisPostId(postDto.getId(), postDto.getPublishedAt()));
                feed.setPostIds(postIds);
                updateRedisFeed(feed);
            } else {
                SortedSet<RedisPostId> postIds = new TreeSet<>();
                postIds.add(new RedisPostId(postDto.getId(), postDto.getPublishedAt()));
                RedisFeed newFeed = RedisFeed.builder()
                        .userId(follower.getId())
                        .postIds(postIds)
                        .build();
                redisFeedRepository.save(newFeed);
            }
        }
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void deletePostFromFeed(PostDto postDto) {
        List<UserDto> followers = userServiceClient.getFollowers(postDto.getAuthorId());
        for (UserDto follower : followers) {
            Optional<RedisFeed> optional = redisFeedRepository.findById(follower.getId());
            if (optional.isPresent()) {
                RedisFeed feed = optional.get();
                SortedSet<RedisPostId> postIds = feed.getPostIds();
                postIds.remove(new RedisPostId(postDto.getId(), postDto.getPublishedAt()));
                feed.setPostIds(postIds);
                updateRedisFeed(feed);
            }
        }
    }

    @Override
    public void saveOrUpdatePostAndAuthorInCache(RedisPost redisPost, RedisUser redisUser) {
        redisPostsTemplate.opsForValue().set(redisPost.getId(), redisPost);
        redisUsersTemplate.opsForValue().set(redisUser.getId(), redisUser);
    }

    @Override
    public void deletePostFromCache(RedisPost redisPost) {
        redisPostsTemplate.delete(redisPost.getId());
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void saveOrUpdatePostInCache(RedisPost redisPost) {
        redisPostsTemplate.opsForValue().set(redisPost.getId(), redisPost);
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public void addPostView(PostDto postDto) {
        Optional<RedisPost> optional = redisPostRepository.findById(postDto.getId());
        if (optional.isPresent()) {
            RedisPost post = optional.get();
            post.getPostDto().incrementViews();
            saveOrUpdateRedisPost(post);
        }
    }

    private void saveOrUpdateRedisPost(RedisPost redisPost) {
        redisPostsTemplate.opsForValue().set(redisPost.getId(), redisPost);
    }

    private void updateRedisFeed(RedisFeed redisFeed) {
        redisFeedTemplate.opsForValue().set(redisFeed.getUserId(), redisFeed);
    }
}
