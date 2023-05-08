package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeDto {
    private Long id;

    @NotNull
    private Long userId;
    private Long postId;
    private Long commentId;
}
