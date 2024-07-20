package faang.school.postservice.entity.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateDto {
    @NotNull
    @NotBlank
    private String content;
    private Long authorId;
    private Long projectId;
}
