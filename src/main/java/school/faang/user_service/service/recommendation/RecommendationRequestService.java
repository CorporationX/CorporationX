package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

public interface RecommendationRequestService {
    RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest);
    List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto requestFilterDto);
    RecommendationRequestDto getRequest(long id);
    RecommendationRequestDto rejectRequest(long id, RejectionDto rejection);
    RecommendationRequest findById(long id);
}
