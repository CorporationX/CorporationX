package school.faang.user_service.service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Component
public class ReceiverFilter extends RecommendationRequestFilter {
    @Override
    protected boolean applyFilter(RecommendationRequest req, RequestFilterDto filter) {
        return filter.getReceiverId().equals(req.getReceiver().getId());
    }

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getReceiverId() != null;
    }
}
