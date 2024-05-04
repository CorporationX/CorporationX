package school.faang.user_service.service.recommendation.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestFilterByReceiver implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestFilterDto) {
        return requestFilterDto.getReceiverId() > 0;
    }

    @Override
    public Stream<RecommendationRequest> filter(Stream<RecommendationRequest> streamRequests, RecommendationRequestFilterDto requestFilterDto) {
        return streamRequests.filter(recommendationRequest -> recommendationRequest.getReceiver().getId() == requestFilterDto.getReceiverId());
    }
}