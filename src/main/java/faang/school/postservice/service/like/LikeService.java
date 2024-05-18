package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeService {
    LikeDto addLikeOnPost(long userId, long postId);
    LikeDto addLikeOnComment(long userId, long commentId);
    void removeLikeFromPost(long likeId, long userId, long postId);
    void removeLikeFromComment(long likeId, long userId, long commentId);
}
