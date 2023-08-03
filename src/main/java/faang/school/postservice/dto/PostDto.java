package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;

    @NotBlank(message = "Content is required")
    @NotNull(message = "Content is required")
    private String content;

    private Long userId;
    private Long projectId;
}
