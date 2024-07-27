package faang.school.postservice.service.redis.like;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.like.DeleteCommentLikeEvent;
import faang.school.postservice.event.like.DeletePostLikeEvent;
import faang.school.postservice.event.like.LikeCommentEvent;
import faang.school.postservice.event.like.LikePostEvent;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.repository.redis.RedisCommentRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeCacheServiceImplTest {

    @InjectMocks
    private LikeCacheServiceImpl likeCacheService;

    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private RedisCommentRepository redisCommentRepository;

    @Mock
    private CommentService commentService;

    @Mock
    private PostService postService;

    @Mock
    private RedisCommentMapper redisCommentMapper;

    @Mock
    private RedisPostMapper redisPostMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(likeCacheService, "postTtl", 60000L);
        ReflectionTestUtils.setField(likeCacheService, "commentTtl", 60000L);
    }

    @Test
    void testAddLikeOnPost_WhenRedisPostExists() {
        Long postId = 1L;
        Long userId = 1L;
        Long authorId = 1L;
        LikePostEvent event = new LikePostEvent(postId, userId, authorId);
        RedisPost redisPost = new RedisPost();
        redisPost.setId(postId);
        redisPost.setLikes(1L);

        when(redisPostRepository.getById(postId)).thenReturn(redisPost);

        likeCacheService.addLikeOnPost(event);

        verify(redisPostRepository).save(postId, redisPost);
        assertEquals(2, redisPost.getLikes());
    }

    @Test
    void testAddLikeOnPost_WhenRedisPostDoesNotExist() {
        Long postId = 1L;
        Long userId = 1L;
        Long authorId = 1L;
        LikePostEvent event = new LikePostEvent(postId, userId, authorId);
        RedisPost redisPost = new RedisPost();
        redisPost.setId(postId);
        redisPost.setLikes(1L);

        when(redisPostRepository.getById(postId)).thenReturn(null);
        when(postService.getById(postId)).thenReturn(new PostDto());
        when(redisPostMapper.toRedisPost(any(PostDto.class))).thenReturn(redisPost);

        likeCacheService.addLikeOnPost(event);

        verify(redisPostRepository, times(2)).save(postId, redisPost);
        assertEquals(2, redisPost.getLikes());
    }

    @Test
    void testDeleteLikeFromPost_WhenRedisPostExists() {
        Long postId = 1L;
        DeletePostLikeEvent event = new DeletePostLikeEvent(postId);
        RedisPost redisPost = new RedisPost();
        redisPost.setId(postId);
        redisPost.setLikes(1L);

        when(redisPostRepository.getById(postId)).thenReturn(redisPost);

        likeCacheService.deleteLikeFromPost(event);

        verify(redisPostRepository).save(postId, redisPost);
        assertEquals(0, redisPost.getLikes());
    }

    @Test
    void testAddLikeToComment_WhenRedisCommentExists() {
        Long commentId = 1L;
        LikeCommentEvent event = new LikeCommentEvent(commentId);
        RedisComment redisComment = new RedisComment();
        redisComment.setId(commentId);
        redisComment.setLikes(1L);

        when(redisCommentRepository.getById(commentId)).thenReturn(redisComment);

        likeCacheService.addLikeToComment(event);

        verify(redisCommentRepository).save(eq(commentId), any(RedisComment.class));
        assertEquals(2, redisComment.getLikes());
    }


    @Test
    void testAddLikeToComment_WhenRedisCommentDoesNotExist() {
        Long commentId = 1L;
        LikeCommentEvent event = new LikeCommentEvent(commentId);
        RedisComment redisComment = new RedisComment();
        redisComment.setId(commentId);
        redisComment.setLikes(1L);

        when(redisCommentRepository.getById(commentId)).thenReturn(null);
        when(commentService.getById(commentId)).thenReturn(new CommentDto());
        when(redisCommentMapper.toRedisDto(any(CommentDto.class))).thenReturn(redisComment);

        likeCacheService.addLikeToComment(event);

        verify(redisCommentRepository, times(2)).save(commentId, redisComment);
        assertEquals(2, redisComment.getLikes());
    }

    @Test
    void testDeleteLikeFromComment_WhenRedisCommentExists() {
        Long commentId = 1L;
        DeleteCommentLikeEvent event = new DeleteCommentLikeEvent(commentId);
        RedisComment redisComment = new RedisComment();
        redisComment.setId(commentId);
        redisComment.setLikes(1L);
        redisComment.setLikes(1L);

        when(redisCommentRepository.getById(commentId)).thenReturn(redisComment);

        likeCacheService.deleteLikeFromComment(event);

        verify(redisCommentRepository).save(commentId, redisComment);
        assertEquals(0, redisComment.getLikes());
    }

    @Test
    void testSaveCommentToRedis() {
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        RedisComment redisComment = new RedisComment();
        redisComment.setId(commentId);
        redisComment.setLikes(1L);

        when(commentService.getById(commentId)).thenReturn(commentDto);
        when(redisCommentMapper.toRedisDto(commentDto)).thenReturn(redisComment);

        RedisComment result = likeCacheService.saveCommentToRedis(commentId);

        verify(redisCommentRepository).save(commentId, redisComment);
        assertEquals(commentId, result.getId());
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

        RedisPost result = likeCacheService.savePostToRedis(postId);

        verify(redisPostRepository).save(postId, redisPost);
        assertEquals(postId, result.getId());
    }
}
