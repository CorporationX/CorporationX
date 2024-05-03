package school.faang.user_service.service.recommendation;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

@Service
public interface RecommendationRequestService {
    RecommendationRequestDto requestRecommendation(RecommendationRequest recommendationRequest);
}
