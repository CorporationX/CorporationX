package school.faang.user_service.service.recommendation.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.HOURS;

@Component
public class RecommendationRequestFilterByUpdatedTime implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestFilterDto) {
        return requestFilterDto.getUpdatedAt() != null;
    }

    @Override
    public Stream<RecommendationRequest> filter(Stream<RecommendationRequest> streamRequests, RecommendationRequestFilterDto requestFilterDto) {
        return streamRequests.filter(recommendationRequest -> recommendationRequest.getUpdatedAt().truncatedTo(HOURS)
                .equals(requestFilterDto.getUpdatedAt().truncatedTo(HOURS)));
    }
}