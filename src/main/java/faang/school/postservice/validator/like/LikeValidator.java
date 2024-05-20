package faang.school.postservice.validator.like;

public interface LikeValidator {

    void validatePostToLike(long userId, long postId);

    void validateCommentToLike(long userId, long commentId);

    void validateUserExistence(long userId);
}
