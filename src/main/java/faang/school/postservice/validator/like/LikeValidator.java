package faang.school.postservice.validator.like;

import faang.school.postservice.entity.model.Comment;
import faang.school.postservice.entity.model.Post;

public interface LikeValidator {

    void validateAndGetPostToLike(long userId, Post post);

    void validateCommentToLike(long userId, Comment comment);

    void validateUserExistence(long userId);
}
