package faang.school.postservice.entity.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private HashSet<Long> likesIds;
    private HashSet<Long> viewersIds;
    private boolean published;
    private LocalDateTime publishedAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
