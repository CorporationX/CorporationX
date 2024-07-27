package faang.school.postservice.event.post;

import faang.school.postservice.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletePostEvent implements Event {
    private Long postId;
    private Long authorId;
    private HashSet<Long> followersIds;
}