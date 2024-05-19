package faang.school.postservice.validator.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeValidatorImpl implements LikeValidator {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;

    public void validateLike(LikeDto likeDto) {
        if (likeDto.getPostId() != null && likeDto.getCommentId() != null) {
            throw new DataValidationException("can't like both post and comment");
        }

        if (likeDto.getPostId() == null && likeDto.getCommentId() == null) {
            throw new DataValidationException("postId or commentId must not be null");
        }
    }

    @Transactional(readOnly = true)
    public void validatePostToLike(long userId, long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("post with postId:" + postId + " not found"));

        boolean isLiked = post.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(likedUserId -> likedUserId == userId);

        if (isLiked) {
            throw new DataValidationException("user with userId:" + userId + " can't like post with postId:" + postId + " two times");
        }
    }

    @Transactional(readOnly = true)
    public void validateCommentToLike(long userId, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("comment with commentId:" + commentId + " not found"));

        boolean isLiked = comment.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(likedUserId -> likedUserId == userId);

        if (isLiked) {
            throw new DataValidationException("user with userId:" + userId + " can't like comment with commentId:" + commentId + " two times");
        }
    }

    public void validateUserExistence(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("can't find user with userId:" + userId);
        }
    }
}
