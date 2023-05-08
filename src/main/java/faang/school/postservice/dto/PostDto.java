package faang.school.postservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Integer likes;
    private LocalDateTime createdAt;
}
