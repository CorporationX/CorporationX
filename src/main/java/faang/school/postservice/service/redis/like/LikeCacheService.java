package faang.school.postservice.service.redis.like;

import faang.school.postservice.event.like.DeleteCommentLikeEvent;
import faang.school.postservice.event.like.DeletePostLikeEvent;
import faang.school.postservice.event.like.LikeCommentEvent;
import faang.school.postservice.event.like.LikePostEvent;

public interface LikeCacheService {

    void addLikeOnPost(LikePostEvent event);

    void deleteLikeFromPost(DeletePostLikeEvent event);

    void addLikeToComment(LikeCommentEvent event);

    void deleteLikeFromComment(DeleteCommentLikeEvent event);
}
