package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final UserServiceClient userServiceClient;

    public void validateCreation(@Valid CommentDto comment) {
        if (comment.getAuthorId() == null || userServiceClient.getUser(comment.getAuthorId()) == null) {
            throw new DataValidationException("Comment author cannot be empty or non existing user");
        }
        if (comment.getPostId() == null) {
            throw new DataValidationException("Comment post cannot be empty");
        }
    }

    public void validateUpdate(Comment comment, @Valid CommentDto dto) {
        if (comment.getAuthorId() != dto.getAuthorId()) {
            throw new DataValidationException("Comment author cannot be changed");
        }
        if (comment.getPost().getId() != dto.getPostId()) {
            throw new DataValidationException("Comment post cannot be changed");
        }
    }
}
