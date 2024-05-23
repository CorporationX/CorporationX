package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(long postId, long userId, CommentDto commentDto);

    List<CommentDto> getAllPostComments(long postId);

    CommentDto updateComment(long commentId, long userId, CommentDto commentDto);

    CommentDto deleteComment(long postId, long commentId, long userId);
}
