package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.entity.recommendation.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    private String message;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SkillRequestDto> skills;
    private long requesterId;
    private long receiverId;
}
