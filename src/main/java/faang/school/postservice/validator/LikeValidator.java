package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.LikeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;

    public void validate(@Valid LikeDto like) {
        if (userServiceClient.getUser(like.getUserId()) == null) {
            throw new DataValidationException("Author with id " + like.getUserId() + " does not exist");
        }
        if (like.getPostId() == null && like.getCommentId() == null) {
            throw new DataValidationException("Post id or comment id is required");
        }
        if (like.getPostId() != null && like.getCommentId() != null) {
            throw new DataValidationException("Only one of post id or comment id is allowed");
        }
        validateDuplicatedPostLike(like);
        validateDuplicatedCommentLike(like);
    }

    private void validateDuplicatedPostLike(LikeDto like) {
        if (like.getPostId() != null) {
            likeRepository.findByPostIdAndUserId(like.getPostId(), like.getUserId())
                    .ifPresent((entity) -> {
                        throw new DataValidationException("User with id " + like.getUserId() + " already liked post with id " + like.getPostId());
                    });
        }
    }

    private void validateDuplicatedCommentLike(LikeDto like) {
        if (like.getCommentId() != null) {
            likeRepository.findByCommentIdAndUserId(like.getCommentId(), like.getUserId())
                    .ifPresent((entity) -> {
                        throw new DataValidationException("User with id " + like.getUserId() + " already liked comment with id " + like.getCommentId());
                    });
        }
    }
}
