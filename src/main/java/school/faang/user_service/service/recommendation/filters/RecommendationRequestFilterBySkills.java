package school.faang.user_service.service.recommendation.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public class RecommendationRequestFilterBySkills implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestFilterDto) {
        List<Long> skillIds = requestFilterDto.getSkillIds();
        return skillIds != null && !skillIds.isEmpty();
    }

    @Override
    public Stream<RecommendationRequest> filter(Stream<RecommendationRequest> streamRequests, RecommendationRequestFilterDto requestFilterDto) {
        return streamRequests.filter(recommendationRequest -> recommendationRequest.getSkills().stream()
                .map(skillRequest -> skillRequest.getSkill().getId())
                .toList().equals(requestFilterDto.getSkillIds()));
    }
}