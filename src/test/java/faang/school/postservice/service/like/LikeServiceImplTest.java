package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.MessagePublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like.LikeValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeValidatorImpl likeValidator;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeMapper mapper;
    @Mock
    private MessagePublisher<LikeEvent> likePostPublisher;
    @InjectMocks
    private LikeServiceImpl likeService;

    private final long userId = 4L;
    private final long postId = 2L;
    private final long commentId = 3L;
    private final long likeId = 1L;
    private LikeDto likeDto;
    private Like like;
    private Post post;
    private Comment comment;

    @BeforeEach
    void init() {
        likeDto = LikeDto.builder()
                .id(likeId)
                .build();

        like = Like.builder()
                .id(likeId)
                .build();

        post = Post.builder()
                .id(2L)
                .likes(new ArrayList<>())
                .build();

        comment = Comment.builder()
                .id(3L)
                .likes(new ArrayList<>())
                .build();
    }

    @Test
    void addLikeOnPost() {
        when(mapper.toEntity(any(LikeDto.class))).thenReturn(like);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.save(like)).thenReturn(like);
        when(mapper.toDto(like)).thenReturn(likeDto);

        LikeDto actual = likeService.addLikeOnPost(userId, postId);
        assertEquals(likeDto, actual);
        assertFalse(post.getLikes().isEmpty());

        InOrder inOrder = inOrder(likeValidator, mapper, likeRepository, postRepository);
        inOrder.verify(likeValidator, times(1)).validateUserExistence(userId);
        inOrder.verify(postRepository, times(1)).findById(postId);
        inOrder.verify(likeValidator, times(1)).validateAndGetPostToLike(userId, post);
        inOrder.verify(mapper, times(1)).toEntity(any(LikeDto.class));
        inOrder.verify(likeRepository, times(1)).save(like);
        inOrder.verify(mapper, times(1)).toDto(like);
    }

    @Test
    void removeLikeFromPost() {
        post.getLikes().add(like);

        when(mapper.toEntity(any(LikeDto.class))).thenReturn(like);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        likeService.removeLikeFromPost(likeId, userId, postId);
        assertTrue(post.getLikes().isEmpty());

        InOrder inOrder = inOrder(likeValidator, mapper, likeRepository, postRepository);
        inOrder.verify(mapper, times(1)).toEntity(any(LikeDto.class));
        inOrder.verify(postRepository, times(1)).findById(postId);
        inOrder.verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void addLikeOnComment() {
        when(mapper.toEntity(any(LikeDto.class))).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(mapper.toDto(like)).thenReturn(likeDto);

        LikeDto actual = likeService.addLikeOnComment(userId, commentId);
        assertEquals(likeDto, actual);
        assertFalse(comment.getLikes().isEmpty());

        InOrder inOrder = inOrder(likeValidator, mapper, likeRepository, commentRepository);
        inOrder.verify(likeValidator, times(1)).validateUserExistence(userId);
        inOrder.verify(commentRepository, times(1)).findById(commentId);
        inOrder.verify(likeValidator, times(1)).validateCommentToLike(userId, comment);
        inOrder.verify(mapper, times(1)).toEntity(any(LikeDto.class));
        inOrder.verify(likeRepository, times(1)).save(like);
        inOrder.verify(mapper, times(1)).toDto(like);
    }

    @Test
    void removeLikeFromComment() {
        comment.getLikes().add(like);

        when(mapper.toEntity(any(LikeDto.class))).thenReturn(like);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        likeService.removeLikeFromComment(likeId, userId, commentId);
        assertTrue(comment.getLikes().isEmpty());

        InOrder inOrder = inOrder(likeValidator, mapper, likeRepository, commentRepository);
        inOrder.verify(mapper, times(1)).toEntity(any(LikeDto.class));
        inOrder.verify(commentRepository, times(1)).findById(commentId);
        inOrder.verify(likeRepository, times(1)).delete(like);
    }
}