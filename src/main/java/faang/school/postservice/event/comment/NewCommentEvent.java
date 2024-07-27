package faang.school.postservice.event.comment;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.model.Comment;
import faang.school.postservice.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.SortedSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentEvent implements Event {
    private Long postId;
    private Long authorId;
    private Long commentId;
    private Long userId;
}