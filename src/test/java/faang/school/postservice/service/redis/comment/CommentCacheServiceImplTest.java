package faang.school.postservice.service.redis.comment;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.comment.DeleteCommentEvent;
import faang.school.postservice.event.comment.NewCommentEvent;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentCacheServiceImplTest {

    @InjectMocks
    private CommentCacheServiceImpl commentCacheService;

    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private RedisPostMapper redisPostMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(commentCacheService, "postTtl", 60000L);
        ReflectionTestUtils.setField(commentCacheService, "maxCommentsInCache", 100);
    }

    @Test
    void testAddCommentToPost_WhenRedisPostExists() {
        Long postId = 1L;
        Long authorId = 2L;
        Long commentId = 2L;
        Long userId = 1L;
        NewCommentEvent event = new NewCommentEvent(postId, authorId, commentId, userId);
        RedisPost redisPost = new RedisPost();
        redisPost.setId(postId);
        redisPost.setCommentsIds(new LinkedHashSet<>());

        when(redisPostRepository.getById(postId)).thenReturn(redisPost);

        commentCacheService.addCommentToPost(event);

        verify(redisPostRepository).save(postId, redisPost);
        assertEquals(1, redisPost.getCommentsIds().size());
        assertEquals(commentId, redisPost.getCommentsIds().iterator().next());
    }

    @Test
    void testAddCommentToPost_WhenRedisPostDoesNotExist() {
        Long postId = 1L;
        Long authorId = 2L;
        Long commentId = 2L;
        Long userId = 1L;
        NewCommentEvent event = new NewCommentEvent(postId, authorId, commentId, userId);
        RedisPost redisPost = new RedisPost();
        redisPost.setId(postId);
        redisPost.setCommentsIds(new LinkedHashSet<>());

        when(redisPostRepository.getById(postId)).thenReturn(null);
        when(postService.getById(postId)).thenReturn(new PostDto());
        when(commentService.getAllPostComments(postId)).thenReturn(List.of());
        when(redisPostMapper.toRedisPost(any(PostDto.class))).thenReturn(redisPost);

        commentCacheService.addCommentToPost(event);

        verify(redisPostRepository, times(2)).save(postId, redisPost);
        assertEquals(1, redisPost.getCommentsIds().size());
        assertEquals(commentId, redisPost.getCommentsIds().iterator().next());
    }

    @Test
    void testDeleteCommentFromPost_WhenRedisPostExists() {
        Long postId = 1L;
        Long commentId = 2L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setPostId(postId);
        DeleteCommentEvent event = new DeleteCommentEvent(commentDto);
        RedisPost redisPost = new RedisPost();
        redisPost.setId(postId);
        redisPost.setCommentsIds(new LinkedHashSet<>(Set.of(commentId)));

        when(redisPostRepository.getById(postId)).thenReturn(redisPost);

        commentCacheService.deleteCommentFromPost(event);

        verify(redisPostRepository).save(postId, redisPost);
        assertEquals(0, redisPost.getCommentsIds().size());
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

        RedisPost result = commentCacheService.savePostToRedis(postId);

        verify(redisPostRepository).save(postId, redisPost);
        assertNotNull(result);
        assertEquals(postId, result.getId());
    }
}
