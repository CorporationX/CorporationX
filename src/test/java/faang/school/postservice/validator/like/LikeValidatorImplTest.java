package faang.school.postservice.validator.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeValidatorImplTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private LikeValidatorImpl validator;

    private final long userId = 1L;
    private final long commentId = 2L;
    private final long postId = 3L;
    private Post post;
    private Comment comment;
    private Like like;

    @BeforeEach
    void init() {
        post = Post.builder()
                .id(postId)
                .likes(new ArrayList<>())
                .build();

        comment = Comment.builder()
                .id(commentId)
                .likes(new ArrayList<>())
                .build();

        like = Like.builder()
                .id(4L)
                .userId(userId)
                .build();
    }

    @Test
    void validateCommentToLikeNotFoundComment() {
        like.setComment(comment);

        NotFoundException e = assertThrows(NotFoundException.class, () -> validator.validateCommentToLike(userId, comment));
        assertEquals(
                "comment with commentId:" + commentId + " not found",
                e.getMessage()
        );
    }

    @Test
    void validateCommentToLikeAlreadyLikedComment() {
        like.setUserId(userId);
        comment.setLikes(List.of(like));
        like.setComment(comment);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validateCommentToLike(userId, comment));
        assertEquals(
                "user with userId:" + userId + " can't like comment with commentId:" + commentId + " two times",
                e.getMessage()
        );
    }

    @Test
    void validateCommentToLike() {
        like.setComment(comment);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> validator.validateCommentToLike(userId, comment));
    }

    @Test
    void validatePostToLikeNotFoundAndGetPost() {
        like.setPost(post);

        NotFoundException e = assertThrows(NotFoundException.class, () -> validator.validateAndGetPostToLike(userId, post));
        assertEquals(
                "post with postId:" + postId + " not found",
                e.getMessage()
        );
    }

    @Test
    void validatePostToLikeAlreadyLikedAndGetPost() {
        post.setLikes(List.of(like));
        like.setPost(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validateAndGetPostToLike(userId, post));
        assertEquals(
                "user with userId:" + userId + " can't like post with postId:" + postId + " two times",
                e.getMessage()
        );
    }

    @Test
    void validateAndGetPostToLike() {
        like.setPost(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> validator.validateAndGetPostToLike(userId, post));
    }

    @Test
    void validateUserExistence() {
        Request request = Request.create(Request.HttpMethod.GET, "url",
                new HashMap<>(), null, new RequestTemplate());

        when(userServiceClient.getUser(userId)).thenThrow(new FeignException.NotFound("", request, null, new HashMap<>()));

        NotFoundException e = assertThrows(NotFoundException.class, () -> validator.validateUserExistence(userId));
        assertEquals(
                "can't find user with userId:" + userId,
                e.getMessage()
        );
    }
}