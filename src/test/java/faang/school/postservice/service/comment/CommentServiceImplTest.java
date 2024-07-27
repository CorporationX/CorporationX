package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.comment.CommentToCreateDto;
import faang.school.postservice.entity.dto.comment.CommentToUpdateDto;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.entity.model.Comment;
import faang.school.postservice.entity.model.Post;
import faang.school.postservice.kafka.producer.NewCommentProducer;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.commonMethods.CommonServiceMethods;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommonServiceMethods commonServiceMethods;

    @Mock
    private NewCommentProducer newCommentPublisher;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private RedisCommentRepository redisCommentRepository;
    @Mock
    private RedisUserRepository redisUserRepository;
    @Mock
    private RedisCommentMapper redisCommentMapper;
    @Mock
    private RedisUserMapper redisUserMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void createComment_success() {
        Long postId = 1L;
        Long userId = 1L;
        CommentToCreateDto commentToCreateDto = new CommentToCreateDto();
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);
        Post post = new Post();
        Comment comment = new Comment();
        UserDto userDto = new UserDto();
        RedisUser redisUser = new RedisUser();
        RedisComment redisComment = new RedisComment();

        when(commonServiceMethods.findEntityById(postRepository, postId, "Post")).thenReturn(post);
        when(commentMapper.toEntity(commentToCreateDto)).thenReturn(comment);
        when(redisUserMapper.toRedisDto(userDto)).thenReturn(redisUser);
        when(redisCommentMapper.toRedisDto(commentDto)).thenReturn(redisComment);
        doNothing().when(commentValidator).validateCreateComment(userId);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(postId, userId, commentToCreateDto);

        verify(commonServiceMethods, times(1)).findEntityById(postRepository, postId, "Post");
        verify(commentMapper, times(1)).toEntity(commentToCreateDto);
        verify(commentValidator, times(1)).validateCreateComment(userId);
        verify(commentRepository, times(1)).save(comment);

        assertEquals(commentDto, result);
    }

    @Test
    void getAllPostComments_success() {
        long postId = 1L;
        Comment comment1 = new Comment();
        comment1.setCreatedAt(LocalDateTime.now().minusDays(1));
        Comment comment2 = new Comment();
        comment2.setCreatedAt(LocalDateTime.now());
        List<Comment> comments = Arrays.asList(comment1, comment2);
        CommentDto commentDto1 = new CommentDto();
        CommentDto commentDto2 = new CommentDto();

        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
        when(commentMapper.toDto(comment1)).thenReturn(commentDto1);
        when(commentMapper.toDto(comment2)).thenReturn(commentDto2);

        List<CommentDto> result = commentService.getAllPostComments(postId);

        verify(commentRepository, times(1)).findAllByPostId(postId);
        verify(commentMapper, times(1)).toDto(comment1);
        verify(commentMapper, times(1)).toDto(comment2);

        assertEquals(Arrays.asList(commentDto1, commentDto2), result);
    }

    @Test
    void updateComment_success() {
        long commentId = 1L;
        long userId = 1L;
        CommentToUpdateDto updatedCommentDto = new CommentToUpdateDto();
        CommentDto commentDto = new CommentDto();
        Comment comment = new Comment();
        Post post = new Post();
        comment.setPost(post);

        when(commonServiceMethods.findEntityById(commentRepository, commentId, "Comment")).thenReturn(comment);
        doNothing().when(commentValidator).validateUpdateAlbum(comment, userId);
        doNothing().when(commentMapper).update(updatedCommentDto, comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(commentId, userId, updatedCommentDto);

        verify(commonServiceMethods, times(1)).findEntityById(commentRepository, commentId, "Comment");
        verify(commentValidator, times(1)).validateUpdateAlbum(comment, userId);
        verify(commentMapper, times(1)).update(updatedCommentDto, comment);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toDto(comment);

        assertEquals(commentDto, result);
    }

    @Test
    void deleteComment_success() {
        long postId = 1L;
        long commentId = 1L;
        long userId = 1L;
        Comment comment = new Comment();
        Post post = new Post();
        comment.setPost(post);
        CommentDto commentDto = new CommentDto();

        when(commonServiceMethods.findEntityById(commentRepository, commentId, "Comment")).thenReturn(comment);
        doNothing().when(commentValidator).validateDeleteAlbum(postId, userId, comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.deleteComment(postId, commentId, userId);

        verify(commonServiceMethods, times(1)).findEntityById(commentRepository, commentId, "Comment");
        verify(commentValidator, times(1)).validateDeleteAlbum(postId, userId, comment);
        verify(commentRepository, times(1)).deleteById(commentId);
        verify(commentMapper, times(1)).toDto(comment);

        assertEquals(commentDto, result);
    }
}
