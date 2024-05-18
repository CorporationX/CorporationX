package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostValidator {
    private final PostRepository postRepository;

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
}
