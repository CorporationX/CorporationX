package school.faang.user_service.service.recommendation.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.*;

@Component
public class RecommendationRequestFilterByCreatedTime implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestFilterDto) {
        return requestFilterDto.getCreatedAt() != null;
    }

    @Override
    public Stream<RecommendationRequest> filter(Stream<RecommendationRequest> streamRequests, RecommendationRequestFilterDto requestFilterDto) {
        return streamRequests.filter(recommendationRequest -> recommendationRequest.getCreatedAt().truncatedTo(HOURS)
                .equals(requestFilterDto.getCreatedAt().truncatedTo(HOURS)));
    }
}