package faang.school.postservice.event.post;

import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PostViewEvent implements Event {
    private PostDto postDto;
    private Long viewerId;
    private LocalDateTime createdAt;
}