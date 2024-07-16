package faang.school.postservice.service.redis.comment;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.model.redis.RedisComment;

import java.util.List;

public interface CommentCacheService {

    void addCommentToPost(CommentDto comment);

    void updateCommentOnPost(CommentDto comment);

    void deleteCommentFromPost(CommentDto commentDto);
}
