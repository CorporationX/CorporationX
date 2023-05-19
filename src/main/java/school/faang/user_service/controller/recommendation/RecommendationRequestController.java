package school.faang.user_service.controller.recommendation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/recommendation/request")
    public RecommendationRequestDto requestRecommendation(@RequestBody @Validated RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @PostMapping("/recommendation/request/list")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody @Validated RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/recommendation/request/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PostMapping("/recommendation/request/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @RequestBody @Validated RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
