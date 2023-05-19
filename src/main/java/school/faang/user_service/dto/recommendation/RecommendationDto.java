package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class RecommendationDto {
    private Long id;
    @NotNull(message = "authorId is not provided")
    private Long authorId;
    @NotNull(message = "receiverId is not provided")
    private Long receiverId;
    @NotBlank(message = "content should not be empty")
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
