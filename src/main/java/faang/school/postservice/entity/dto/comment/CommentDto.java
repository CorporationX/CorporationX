package faang.school.postservice.entity.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;
    private String content;
    private Long authorId;
    private Long likesCount;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
