package faang.school.postservice.event.comment;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.SortedSet;

@Data
@Builder
@AllArgsConstructor
public class DeleteCommentEvent implements Event {
    private CommentDto commentDto;
//    private SortedSet<Long> commentLikesIds;
}