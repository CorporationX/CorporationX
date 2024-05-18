package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostValidator validator;

    private final long userId = 1L;
    private final long postId = 2L;
    private Post post;
    private Like like;

    @BeforeEach
    void init() {
        post = Post.builder()
                .id(3L)
                .likes(new ArrayList<>())
                .build();

        like = Like.builder()
                .id(4L)
                .userId(userId)
                .post(post)
                .build();
    }

    @Test
    void validatePostToLikeNotFoundPost() {
    EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> validator.validatePostToLike(userId, postId));
        assertEquals(
                "post with postId:" + postId + " not found",
                e.getMessage()
        );
    }

    @Test
    void validatePostToLikeAlreadyLikedPost() {
        post.setLikes(List.of(like));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validatePostToLike(userId, postId));
        assertEquals(
                "user with userId:" + userId + " can't like post with postId:" + postId + " two times",
                e.getMessage()
        );
    }

    @Test
    void validatePostToLike() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> validator.validatePostToLike(userId, postId));
    }
}