package faang.school.postservice.validator.like;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeValidator {

    void validateLike(LikeDto likeDto);

    void validatePostToLike(long userId, long postId);

    void validateCommentToLike(long userId, long commentId);

    void validateUserExistence(long userId);
}
