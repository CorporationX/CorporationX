package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeValidator validator;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeMapper mapper;
    @InjectMocks
    private LikeServiceImpl likeService;

    private LikeDto likeDto;
    private Like like;
    private Post post;
    private Comment comment;

    @BeforeEach
    void init() {
        likeDto = LikeDto.builder()
                .id(1L)
                .userId(2L)
                .build();

        like = Like.builder()
                .id(1L)
                .userId(2L)
                .build();

        post = Post.builder()
                .id(3L)
                .likes(new ArrayList<>())
                .build();

        comment = Comment.builder()
                .id(4L)
                .likes(new ArrayList<>())
                .build();
    }

    @Test
    void addLikeOnPost() {
        likeDto.setPostId(post.getId());

        when(mapper.toEntity(likeDto)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);
        when(postRepository.findById(likeDto.getPostId())).thenReturn(Optional.of(post));
        when(mapper.toDto(like)).thenReturn(likeDto);

        LikeDto actual = likeService.addLikeOnPost(likeDto);
        assertEquals(likeDto, actual);
        assertFalse(post.getLikes().isEmpty());

        InOrder inOrder = inOrder(validator, mapper, likeRepository, postRepository);
        inOrder.verify(validator, times(1)).validate(likeDto);
        inOrder.verify(mapper, times(1)).toEntity(likeDto);
        inOrder.verify(postRepository, times(1)).findById(likeDto.getPostId());
        inOrder.verify(likeRepository, times(1)).save(like);
        inOrder.verify(mapper, times(1)).toDto(like);
    }

    @Test
    void removeLikeFromPost() {
        likeDto.setPostId(post.getId());
        post.getLikes().add(like);

        when(mapper.toEntity(likeDto)).thenReturn(like);
        when(postRepository.findById(likeDto.getPostId())).thenReturn(Optional.of(post));

        likeService.removeLikeFromPost(likeDto);
        assertTrue(post.getLikes().isEmpty());

        InOrder inOrder = inOrder(validator, mapper, likeRepository, postRepository);
        inOrder.verify(validator, times(1)).validate(likeDto);
        inOrder.verify(mapper, times(1)).toEntity(likeDto);
        inOrder.verify(postRepository, times(1)).findById(likeDto.getPostId());
        inOrder.verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void addLikeOnComment() {
        likeDto.setCommentId(comment.getId());

        when(mapper.toEntity(likeDto)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);
        when(commentRepository.findById(likeDto.getCommentId())).thenReturn(Optional.of(comment));
        when(mapper.toDto(like)).thenReturn(likeDto);

        LikeDto actual = likeService.addLikeOnComment(likeDto);
        assertEquals(likeDto, actual);
        assertFalse(comment.getLikes().isEmpty());

        InOrder inOrder = inOrder(validator, mapper, likeRepository, commentRepository);
        inOrder.verify(validator, times(1)).validate(likeDto);
        inOrder.verify(mapper, times(1)).toEntity(likeDto);
        inOrder.verify(commentRepository, times(1)).findById(likeDto.getCommentId());
        inOrder.verify(likeRepository, times(1)).save(like);
        inOrder.verify(mapper, times(1)).toDto(like);
    }

    @Test
    void removeLikeFromComment() {
        likeDto.setCommentId(comment.getId());
        comment.getLikes().add(like);

        when(mapper.toEntity(likeDto)).thenReturn(like);
        when(commentRepository.findById(likeDto.getCommentId())).thenReturn(Optional.of(comment));

        likeService.removeLikeFromComment(likeDto);
        assertTrue(comment.getLikes().isEmpty());

        InOrder inOrder = inOrder(validator, mapper, likeRepository, commentRepository);
        inOrder.verify(validator, times(1)).validate(likeDto);
        inOrder.verify(mapper, times(1)).toEntity(likeDto);
        inOrder.verify(commentRepository, times(1)).findById(likeDto.getCommentId());
        inOrder.verify(likeRepository, times(1)).delete(like);
    }
}