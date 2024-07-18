package faang.school.postservice.event.like;

import faang.school.postservice.entity.dto.like.LikeDto;
import faang.school.postservice.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.SortedSet;

@Data
@Builder
@AllArgsConstructor
public class DeleteCommentLikeEvent implements Event {
    private LikeDto likeDto;
//    private SortedSet<Long> commentLikesIds;
}
