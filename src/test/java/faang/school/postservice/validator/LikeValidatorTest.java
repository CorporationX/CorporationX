package faang.school.postservice.validator;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class LikeValidatorTest {
    @Spy
    private LikeValidator likeValidator;

    private LikeDto likeDto;

    @BeforeEach
    void init() {
        likeDto = LikeDto.builder()
                .id(1L)
                .userId(2L)
                .build();
    }

    @Test
    void validateNonNullCommentAndPost() {
        likeDto.setPostId(0L);
        likeDto.setCommentId(0L);

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
}