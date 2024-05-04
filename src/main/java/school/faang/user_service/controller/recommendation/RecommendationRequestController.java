package school.faang.user_service.controller.recommendation;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequest recommendationRequest) {
        return recommendationRequestService.requestRecommendation(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto requestFilterDto) {
        return recommendationRequestService.getRecommendationRequests(requestFilterDto);
    }
}