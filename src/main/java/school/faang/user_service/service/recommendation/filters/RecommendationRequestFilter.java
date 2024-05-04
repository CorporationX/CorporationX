package school.faang.user_service.service.recommendation.filters;

import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RecommendationRequestFilterDto requestFilterDto);
    Stream<RecommendationRequest> filter(Stream<RecommendationRequest> streamRequests, RecommendationRequestFilterDto requestFilterDto);
}