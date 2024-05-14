package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeValidatorTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private LikeValidator likeValidator;

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
    void validateNotFoundUser() {
        Request request = Request.create(Request.HttpMethod.GET, "url",
                new HashMap<>(), null, new RequestTemplate());

        when(userServiceClient.getUser(anyLong())).thenThrow(new FeignException.NotFound("", request, null, new HashMap<>()));

        DataValidationException e = assertThrows(DataValidationException.class, () -> likeValidator.validate(likeDto));
        assertEquals(
                "can't add like from user with userId:" + likeDto.getUserId() + " because user doesn't exist",
                e.getMessage()
        );
    }

    @Test
    void validateNonNullCommentAndPost() {
        likeDto.setPostId(post.getId());
        likeDto.setCommentId(comment.getId());

        DataValidationException e = assertThrows(DataValidationException.class, () -> likeValidator.validate(likeDto));
        assertEquals(
                "can't like both post and comment",
                e.getMessage()
        );
    }

    @Test
    void validateNullCommentAndPost() {
        DataValidationException e = assertThrows(DataValidationException.class, () -> likeValidator.validate(likeDto));
        assertEquals(
                "postId or commentId must not be null",
                e.getMessage()
        );
    }

    @Test
    void validateNonNullPost() {
        likeDto.setPostId(post.getId());

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> likeValidator.validate(likeDto));
    }

    @Test
    void validateNonNullComment() {
        likeDto.setCommentId(comment.getId());

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> likeValidator.validate(likeDto));
    }

    @Test
    void validatePostToLikeNullPostId() {
        assertDoesNotThrow(() -> likeValidator.validatePostToLike(likeDto));
    }

    @Test
    void validatePostToLikeNotFoundPost() {
        likeDto.setPostId(post.getId());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> likeValidator.validatePostToLike(likeDto));
        assertEquals(
                "post with postId:" + likeDto.getPostId() + " not found",
                e.getMessage()
        );
    }

    @Test
    void validatePostToLikeAlreadyLikedPost() {
        likeDto.setPostId(post.getId());
        post.setLikes(List.of(like));

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        DataValidationException e = assertThrows(DataValidationException.class, () -> likeValidator.validatePostToLike(likeDto));
        assertEquals(
                "user with userId:" + likeDto.getUserId() + " can't like post with postId:" + likeDto.getPostId() + " two times",
                e.getMessage()
        );
    }

    @Test
    void validatePostToLike() {
        likeDto.setPostId(post.getId());

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> likeValidator.validatePostToLike(likeDto));
    }

    @Test
    void validateCommentToLikeNullCommentId() {
        assertDoesNotThrow(() -> likeValidator.validateCommentToLike(likeDto));
    }

    @Test
    void validateCommentToLikeNotFoundComment() {
        likeDto.setCommentId(comment.getId());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> likeValidator.validateCommentToLike(likeDto));
        assertEquals(
                "comment with commentId:" + likeDto.getCommentId() + " not found",
                e.getMessage()
        );
    }

    @Test
    void validateCommentToLikeAlreadyLikedComment() {
        likeDto.setCommentId(comment.getId());
        comment.setLikes(List.of(like));

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        DataValidationException e = assertThrows(DataValidationException.class, () -> likeValidator.validateCommentToLike(likeDto));
        assertEquals(
                "user with userId:" + likeDto.getUserId() + " can't like comment with commentId:" + likeDto.getCommentId() + " two times",
                e.getMessage()
        );
    }

    @Test
    void validateCommentToLike() {
        likeDto.setCommentId(comment.getId());

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> likeValidator.validateCommentToLike(likeDto));
    }
}