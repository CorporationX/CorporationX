package faang.school.postservice.validator;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {
    //Валидаторы сюда

    public void validate(LikeDto likeDto) {
        if (likeDto.getPostId() != null && likeDto.getCommentId() != null) {
            throw new DataValidationException("can't like both post and comment");
        }

        if (likeDto.getPostId() == null && likeDto.getCommentId() == null) {
            throw new DataValidationException("postId or commentId must not be null");
        }
    }
}
