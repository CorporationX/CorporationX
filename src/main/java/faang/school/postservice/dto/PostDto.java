package faang.school.postservice.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class PostDto {
    private Long id;

    @NotNull
    @NotBlank
    @Length(max = 4096)
    private String content;
    private Long authorId;
    private Long projectId;
    private Integer likes;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
}
