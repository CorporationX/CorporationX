package school.faang.user_service.service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Component
public class SkillFilter extends RecommendationRequestFilter {
    @Override
    protected boolean applyFilter(RecommendationRequest req, RequestFilterDto filter) {
        return req.getSkills().stream()
                .map(SkillRequest::getId)
                .anyMatch(id -> filter.getSkills().contains(id));
    }

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getSkills() != null && !filter.getSkills().isEmpty();
    }
}
