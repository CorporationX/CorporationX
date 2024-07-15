package faang.school.postservice.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private long id;
    private String content;
    private long authorId;
    private long likesCount;
    private long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
