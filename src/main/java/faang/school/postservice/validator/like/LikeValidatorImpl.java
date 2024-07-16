package faang.school.postservice.validator.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.entity.model.Comment;
import faang.school.postservice.entity.model.Like;
import faang.school.postservice.entity.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeValidatorImpl implements LikeValidator {

    private final UserServiceClient userServiceClient;

    @Transactional(readOnly = true)
    public void validateAndGetPostToLike(long userId, Post post) {
        boolean isLiked = post.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(likedUserId -> likedUserId == userId);

        if (isLiked) {
            throw new DataValidationException("user with userId:" + userId + " can't like post with postId:" + post.getId() + " two times");
        }
    }

    @Transactional(readOnly = true)
    public void validateCommentToLike(long userId, Comment comment) {
        boolean isLiked = comment.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(likedUserId -> likedUserId == userId);

        if (isLiked) {
            throw new DataValidationException("user with userId:" + userId + " can't like comment with commentId:" + comment.getId() + " two times");
        }
    }

    public void validateUserExistence(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new NotFoundException("can't find user with userId:" + userId);
        }
    }
}
