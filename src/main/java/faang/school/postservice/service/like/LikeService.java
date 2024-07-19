package faang.school.postservice.service.like;

import faang.school.postservice.entity.dto.like.LikeDto;

import java.util.HashSet;
import java.util.List;

public interface LikeService {

    LikeDto addLikeOnPost(long userId, long postId);

    LikeDto addLikeOnComment(long userId, long commentId);

    void removeLikeFromPost(long likeId, long userId, long postId);

    void removeLikeFromComment(long likeId, long userId, long commentId);

    List<LikeDto> getAllPostLikes (long postId);

    List<LikeDto> getAllCommentLikes (long commentId);
}
