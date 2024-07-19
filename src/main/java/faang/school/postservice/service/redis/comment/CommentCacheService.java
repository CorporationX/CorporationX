package faang.school.postservice.service.redis.comment;

import faang.school.postservice.event.comment.DeleteCommentEvent;
import faang.school.postservice.event.comment.NewCommentEvent;
import faang.school.postservice.event.comment.UpdateCommentEvent;

public interface CommentCacheService {

    void addCommentToPost(NewCommentEvent event);

    void deleteCommentFromPost(DeleteCommentEvent event);
}
