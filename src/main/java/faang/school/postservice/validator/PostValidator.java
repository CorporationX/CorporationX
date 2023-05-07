package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;

    public void validateCreation(PostDto post) {
        validateBasics(post);
        if (post.getAuthorId() != null && userServiceClient.getUser(post.getAuthorId()) == null) {
            throw new DataValidationException("Post author must be a valid user");
        }
        if (post.getProjectId() != null && userServiceClient.getUser(post.getProjectId()) == null) {
            throw new DataValidationException("Post project must be a valid project");
        }
    }

    public void validateUpdate(Post entity, PostDto post) {
        if (post.getAuthorId() != null && !post.getAuthorId().equals(entity.getAuthorId())) {
            throw new DataValidationException("Post author cannot be changed");
        }
        if (post.getProjectId() != null && !post.getProjectId().equals(entity.getProjectId())) {
            throw new DataValidationException("Post project cannot be changed");
        }
        validateBasics(post);
    }

    private void validateBasics(PostDto post) {
        if (post.getContent() == null || post.getContent().isBlank()) {
            throw new DataValidationException("Post content cannot be empty");
        }
        if (post.getAuthorId() == null && post.getProjectId() == null) {
            throw new DataValidationException("Post must be associated with either a user or a project");
        }
        if (post.getAuthorId() != null && post.getProjectId() != null) {
            throw new DataValidationException("Post cannot be associated with both a user and a project");
        }
    }
}
