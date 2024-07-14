package faang.school.postservice.service.redis.like;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeCacheService {

    void addLikeOnPost(LikeDto likeDto);

    void deleteLikeFromPost(long postId);

    void addLikeToComment(long postId, long commentId);

    void deleteLikeFromComment(long postId, long commentId);
}
