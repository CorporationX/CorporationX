package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

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

    @PostMapping("/recommendation/request/list")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/recommendation/request/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PostMapping("/recommendation/request/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    private boolean validateRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        return true;
    }
}
