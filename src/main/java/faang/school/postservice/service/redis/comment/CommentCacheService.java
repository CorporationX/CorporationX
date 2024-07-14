package faang.school.postservice.service.redis.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.redis.comment.RedisCommentDto;

import java.util.List;

public interface CommentCacheService {

    void addCommentToPost(CommentDto comment);

    void updateCommentOnPost(CommentDto comment);

    void deleteCommentFromPost(CommentDto commentDto);

    List<RedisCommentDto> getCachedComments(List<CommentDto> comments);
}
