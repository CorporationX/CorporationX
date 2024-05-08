package school.faang.user_service.controller.recommendation;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.requestRecommendation(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto requestFilterDto) {
        return recommendationRequestService.getRecommendationRequests(requestFilterDto);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        checkRejectionDto(rejection);
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    private void checkRejectionDto(RejectionDto rejection) {
        if (rejection.getReason() == null || rejection.getReason().isBlank()) {
            throw new DataValidationException("rejection should contain the reason");
        }
    }
}