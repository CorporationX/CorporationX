package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
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
class CommentValidatorTest {
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentValidator validator;

    private final long userId = 1L;
    private final long commentId = 2L;
    private Comment comment;
    private Like like;

    @BeforeEach
    void init() {
        comment = Comment.builder()
                .id(3L)
                .likes(new ArrayList<>())
                .build();

        like = Like.builder()
                .id(4L)
                .comment(comment)
                .build();
    }

    @Test
    void validateCommentToLikeNotFoundComment() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> validator.validateCommentToLike(userId, commentId));
        assertEquals(
                "comment with commentId:" + commentId + " not found",
                e.getMessage()
        );
    }

    @Test
    void validateCommentToLikeAlreadyLikedComment() {
        like.setUserId(userId);
        comment.setLikes(List.of(like));

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validateCommentToLike(userId, commentId));
        assertEquals(
                "user with userId:" + userId + " can't like comment with commentId:" + commentId + " two times",
                e.getMessage()
        );
    }

    @Test
    void validateCommentToLike() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> validator.validateCommentToLike(userId, commentId));
    }
}