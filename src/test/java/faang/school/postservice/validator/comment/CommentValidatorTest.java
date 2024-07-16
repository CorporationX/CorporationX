package faang.school.postservice.validator.comment;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.entity.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.user.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {

    @Mock
    private UserValidator userValidator;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentValidator commentValidator;

    @Test
    void validateCreateComment_userExists_noExceptionThrown() {
        doNothing().when(userValidator).validateUserExistence(anyLong());

        assertDoesNotThrow(() -> commentValidator.validateCreateComment(1L));

        verify(userValidator, times(1)).validateUserExistence(1L);
    }

    @Test
    void validateCreateComment_userDoesNotExist_exceptionThrown() {
        doThrow(new DataValidationException("User does not exist")).when(userValidator).validateUserExistence(anyLong());

        assertThrows(DataValidationException.class, () -> commentValidator.validateCreateComment(1L));

        verify(userValidator, times(1)).validateUserExistence(1L);
    }

    @Test
    void validateUpdateAlbum_authorIsCorrect_noExceptionThrown() {
        Comment comment = new Comment();
        comment.setAuthorId(1L);

        doNothing().when(userValidator).validateUserExistence(anyLong());

        assertDoesNotThrow(() -> commentValidator.validateUpdateAlbum(comment, 1L));

        verify(userValidator, times(1)).validateUserExistence(1L);
    }

    @Test
    void validateUpdateAlbum_authorIsIncorrect_exceptionThrown() {
        Comment comment = new Comment();
        comment.setAuthorId(2L);

        doNothing().when(userValidator).validateUserExistence(anyLong());

        assertThrows(DataValidationException.class, () -> commentValidator.validateUpdateAlbum(comment, 1L));

        verify(userValidator, times(1)).validateUserExistence(1L);
    }

    @Test
    void validateDeleteAlbum_postHasComment_noExceptionThrown() {
        Comment comment = new Comment();
        comment.setId(1L);

        doNothing().when(userValidator).validateUserExistence(anyLong());
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(List.of(comment));

        assertDoesNotThrow(() -> commentValidator.validateDeleteAlbum(1L, 1L, comment));

        verify(userValidator, times(1)).validateUserExistence(1L);
        verify(commentRepository, times(1)).findAllByPostId(1L);
    }

    @Test
    void validateDeleteAlbum_postDoesNotHaveComment_exceptionThrown() {
        Comment comment = new Comment();
        comment.setId(1L);

        doNothing().when(userValidator).validateUserExistence(anyLong());
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(List.of());

        assertThrows(DataValidationException.class, () -> commentValidator.validateDeleteAlbum(1L, 1L, comment));

        verify(userValidator, times(1)).validateUserExistence(1L);
        verify(commentRepository, times(1)).findAllByPostId(1L);
    }
}
