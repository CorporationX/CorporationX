package faang.school.postservice.service.redis.feed;

import faang.school.postservice.client.UserServiceClient;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedCacheServiceImplTest {

    @InjectMocks
    private FeedCacheServiceImpl feedCacheService;

    @Mock
    private RedisFeedRepository redisFeedRepository;

    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private RedisUserRepository redisUserRepository;

    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentService commentService;

    @Mock
    private RedisPostMapper redisPostMapper;

    @Mock
    private RedisUserMapper redisUserMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedCacheService, "postTtl", 60000L);
        ReflectionTestUtils.setField(feedCacheService, "userTtl", 60000L);
        ReflectionTestUtils.setField(feedCacheService, "feedTtl", 60000L);
        ReflectionTestUtils.setField(feedCacheService, "postsFeedSize", 100);
        ReflectionTestUtils.setField(feedCacheService, "fetchPostsBatch", 10);
    }

    @Test
    void testGetNewsFeed_WhenRedisFeedExists() {
        Long userId = 1L;
        Long lastPostId = null;
        RedisFeed redisFeed = new RedisFeed();
        redisFeed.setUserId(userId);
        redisFeed.setPostsIds(new LinkedHashSet<>(Set.of(1L, 2L, 3L)));

        when(redisFeedRepository.getById(userId)).thenReturn(redisFeed);
        when(redisPostRepository.getById(any(Long.class))).thenAnswer(invocation -> {
            Long postId = invocation.getArgument(0);
            RedisPost post = new RedisPost();
            post.setId(postId);
            return post;
        });

        LinkedHashSet<RedisPost> posts = feedCacheService.getNewsFeed(userId, lastPostId);

        assertNotNull(posts);
        assertEquals(3, posts.size());
    }

    @Test
    void testGetNewsFeed_WhenRedisFeedDoesNotExist() {
        Long userId = 1L;
        Long lastPostId = null;

        when(redisFeedRepository.getById(userId)).thenReturn(null);
        when(postService.findUserFollowingsPosts(userId, LocalDateTime.now(), 500)).thenReturn(new ArrayList<>());

        LinkedHashSet<RedisPost> posts = feedCacheService.getNewsFeed(userId, lastPostId);

        assertNotNull(posts);
        assertEquals(0, posts.size());
    }

    @Test
    void testAddPostInFeed() {
        Long postId = 1L;
        Long authorId = 2L;
        HashSet<Long> likesIds = new HashSet<>(Arrays.asList(3L, 4L));
        HashSet<Long> viewersIds = new HashSet<>();
        HashSet<Long> followersIds = new HashSet<>(Arrays.asList(4L));
        NewPostEvent event = new NewPostEvent(postId, "B", authorId, null, likesIds, viewersIds, LocalDateTime.now(), followersIds);
        RedisPost redisPost = new RedisPost();
        redisPost.setId(postId);
        RedisUser redisUser = new RedisUser();
        redisUser.setId(authorId);

        when(redisPostRepository.getById(postId)).thenReturn(null);
        when(redisUserRepository.getById(authorId)).thenReturn(null);
        when(postService.getById(postId)).thenReturn(new PostDto());
        when(redisPostMapper.toRedisPost(any(PostDto.class))).thenReturn(redisPost);

        feedCacheService.addPostInFeed(event);

        verify(redisPostRepository, times(2)).save(any(Long.class), any(RedisPost.class));
        verify(redisUserRepository).save(any(Long.class), any(RedisUser.class));
        verify(redisFeedRepository, times(3)).save(any(Long.class), any(RedisFeed.class));
    }


    @Test
    void testDeletePostFromFeed() {
        Long postId = 1L;
        Long authorId = 1L;
        HashSet<Long> followersIds = new HashSet<>(Arrays.asList(3L, 4L));
        DeletePostEvent event = new DeletePostEvent(postId, authorId, followersIds);
        RedisPost redisPost = new RedisPost();
        redisPost.setId(postId);

        when(redisPostRepository.getById(postId)).thenReturn(redisPost);

        feedCacheService.deletePostFromFeed(event);

        verify(redisPostRepository).save(any(Long.class), any(RedisPost.class));
        verify(redisFeedRepository, times(3)).save(any(Long.class), any(RedisFeed.class));
    }

    @Test
    void testAddPostView() {
        Long postId = 1L;
        Long authorId = 1L;
        Long viewerId = 2L;
        PostViewEvent event = new PostViewEvent(postId, authorId, null, viewerId, LocalDateTime.now());
        RedisPost redisPost = new RedisPost();
        redisPost.setId(postId);
        redisPost.setViewersIds(new HashSet<>());

        when(redisPostRepository.getById(postId)).thenReturn(redisPost);

        feedCacheService.addPostView(event);

        verify(redisPostRepository).save(any(Long.class), any(RedisPost.class));
        assertTrue(redisPost.getViewersIds().contains(viewerId));
    }

    @Test
    void testSavePostToRedis() {
        Long postId = 1L;
        PostDto postDto = new PostDto();
        RedisPost redisPost = new RedisPost();
        redisPost.setId(postId);

        when(postService.getById(postId)).thenReturn(postDto);
        when(commentService.getAllPostComments(postId)).thenReturn(List.of());
        when(redisPostMapper.toRedisPost(postDto)).thenReturn(redisPost);

        RedisPost result = feedCacheService.savePostToRedis(postId);

        verify(redisPostRepository).save(postId, redisPost);
        assertNotNull(result);
        assertEquals(postId, result.getId());
    }

    @Test
    void testSaveUserToRedis() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        RedisUser redisUser = new RedisUser();

        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(userServiceClient.getFollowings(userId)).thenReturn(List.of(new UserDto(), new UserDto()));
        when(userServiceClient.getFollowers(userId)).thenReturn(List.of(new UserDto(), new UserDto()));
        when(redisUserMapper.toRedisDto(userDto)).thenReturn(redisUser);

        feedCacheService.saveUserToRedis(userId);

        verify(redisUserRepository).save(userId, redisUser);
        assertNotNull(redisUser);
        assertEquals(userId, redisUser.getId());
    }
}
