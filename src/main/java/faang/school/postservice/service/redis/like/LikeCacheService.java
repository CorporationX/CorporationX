package faang.school.postservice.service.redis.like;

import faang.school.postservice.entity.dto.like.LikeDto;

public interface LikeCacheService {

    void addLikeOnPost(LikeDto likeDto);

    void deleteLikeFromPost(LikeDto likeDto);

    void addLikeToComment(long postId, long commentId);

    void deleteLikeFromComment(long postId, long commentId);
}
