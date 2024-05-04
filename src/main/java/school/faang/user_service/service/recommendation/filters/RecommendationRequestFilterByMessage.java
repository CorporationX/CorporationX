package school.faang.user_service.service.recommendation.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestFilterByMessage implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestFilterDto) {
        return requestFilterDto.getMessage() != null && !requestFilterDto.getMessage().isBlank();
    }

    @Override
    public Stream<RecommendationRequest> filter(Stream<RecommendationRequest> streamRequests, RecommendationRequestFilterDto requestFilterDto) {
        return streamRequests.filter(recommendationRequest -> recommendationRequest.getMessage().equals(requestFilterDto.getMessage()));
    }
}