package faang.school.postservice.event.post;

import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.SortedSet;

@Data
@Builder
@AllArgsConstructor
public class UpdatePostEvent implements Event {
    private PostDto postDto;
    private SortedSet<Long> followersIds;
}