package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostCreateDto {
    private String content;
    private Long authorId;
    private Long projectId;
}
