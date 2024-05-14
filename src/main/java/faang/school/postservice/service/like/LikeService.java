package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeService {
    LikeDto addLikeOnPost(LikeDto likeDto);
    LikeDto addLikeOnComment(LikeDto likeDto);
    void removeLikeFromPost(LikeDto likeDto);
    void removeLikeFromComment(LikeDto likeDto);
}
