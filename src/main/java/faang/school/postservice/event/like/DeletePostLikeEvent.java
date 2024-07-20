package faang.school.postservice.event.like;

import faang.school.postservice.entity.dto.like.LikeDto;
import faang.school.postservice.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DeletePostLikeEvent implements Event {
    private Long postId;
}
