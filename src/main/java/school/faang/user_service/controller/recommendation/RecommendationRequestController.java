package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/recommendation/request")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        if (validateRecommendationRequest(recommendationRequest)) {
            return recommendationRequestService.create(recommendationRequest);
        }
        throw new DataValidationException("Invalid recommendation request data is provided");
    }

    private boolean validateRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        return true;
    }
}
