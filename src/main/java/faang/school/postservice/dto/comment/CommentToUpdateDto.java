package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentToUpdateDto {
    @NotNull(message = "Content cannot be null")
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 4096, message = "Content cannot exceed 4096 characters")
    private String content;
}
