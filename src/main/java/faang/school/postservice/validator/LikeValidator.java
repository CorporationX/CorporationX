package faang.school.postservice.validator;

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

@Component
@RequiredArgsConstructor
public class LikeValidator {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;

    public void validate(LikeDto likeDto) {
        try {
            userServiceClient.getUser(likeDto.getUserId());
        } catch (FeignException e) {
            throw new DataValidationException("can't add like from user with userId:" + likeDto.getUserId() + " because user doesn't exist");
        }

        if (likeDto.getPostId() != null && likeDto.getCommentId() != null) {
            throw new DataValidationException("can't like both post and comment");
        }

        if (likeDto.getPostId() == null && likeDto.getCommentId() == null) {
            throw new DataValidationException("postId or commentId must not be null");
        }

        validatePostToLike(likeDto);

        validateCommentToLike(likeDto);
    }

    public void validatePostToLike(LikeDto likeDto) {
        if (likeDto.getPostId() == null) {
            return;
        }

        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("post with postId:" + likeDto.getPostId() + " not found"));

        boolean isLiked = post.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(userId -> userId.equals(likeDto.getUserId()));

        if (isLiked) {
            throw new DataValidationException("user with userId:" + likeDto.getUserId() + " can't like post with postId:" + likeDto.getPostId() + " two times");
        }
    }

    public void validateCommentToLike(LikeDto likeDto) {
        if (likeDto.getCommentId() == null) {
            return;
        }

        Comment comment = commentRepository.findById(likeDto.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException("comment with commentId:" + likeDto.getCommentId() + " not found"));

        boolean isLiked = comment.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(userId -> userId.equals(likeDto.getUserId()));

        if (isLiked) {
            throw new DataValidationException("user with userId:" + likeDto.getUserId() + " can't like comment with commentId:" + likeDto.getCommentId() + " two times");
        }
    }
}
