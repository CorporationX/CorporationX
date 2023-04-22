package school.faang.user_service.dto.recommendation;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationDto {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
