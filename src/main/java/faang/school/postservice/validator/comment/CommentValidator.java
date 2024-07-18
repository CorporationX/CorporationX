package faang.school.postservice.validator.comment;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.entity.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserValidator userValidator;
    private final CommentRepository commentRepository;

    public void validateCreateComment(long userId) {
        userValidator.validateUserExistence(userId);
    }

    public void validateUpdateAlbum(Comment commentToUpdate, long userId) {
        userValidator.validateUserExistence(userId);
        validateAuthorIsCorrect(commentToUpdate, userId, "update");
    }

    public void validateDeleteAlbum(long postId, long userId, Comment comment) {
        userValidator.validateUserExistence(userId);
        validatePostHasThisComment(postId, comment);
    }

    private void validateAuthorIsCorrect(Comment commentToUpdate, long userId, String errorDescription) {
        if (commentToUpdate.getAuthorId() != userId) {
            throw new DataValidationException(String.format("Only author can %s comment", errorDescription));
        }
    }

    private void validatePostHasThisComment(long postId, Comment comment) {
        List<Comment> commentList = commentRepository.findAllByPostId(postId);
        if (!commentList.contains(comment)) {
            throw new DataValidationException(String.format("Post %d don't have comment %d", postId, comment.getId()));
        }
    }
}
