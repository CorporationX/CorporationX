package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RecommendationRequestDto {
    private Long id;
    @NotBlank(message = "message should not be blank")
    private String message;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SkillRequestDto> skills;
    @NotNull(message = "requesterId should not be null")
    private long requesterId;
    @NotNull(message = "receiverId should not be null")
    private long receiverId;
}
