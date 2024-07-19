package faang.school.postservice.event.post;

import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@Builder
@AllArgsConstructor
public class DeletePostEvent implements Event {
    private PostDto postDto;
    private HashSet<Long> followersIds;
}