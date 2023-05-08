package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;

    @NotBlank(message = "Comment content is mandatory")
    @Length(max = 4096, message = "Comment content cannot be longer than 4096 characters")
    private String content;

    @NotNull(message = "Comment author id is mandatory")
    private Long authorId;

    @NotNull(message = "Comment post id is mandatory")
    private Long postId;

    private LocalDateTime createdAt;
}
