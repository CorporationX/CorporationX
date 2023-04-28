package school.faang.user_service.service.filter.recommendation;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public abstract class RequestFilter {

    public Stream<RecommendationRequest> applyFilter(Stream<RecommendationRequest> reqs, RequestFilterDto filter) {
        return reqs.filter(req -> applyFilter(req, filter));
    }

    protected abstract boolean applyFilter(RecommendationRequest req, RequestFilterDto filter);

    public abstract boolean isApplicable(RequestFilterDto filter);
}
