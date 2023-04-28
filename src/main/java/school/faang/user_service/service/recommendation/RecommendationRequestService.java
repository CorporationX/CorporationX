package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import school.faang.user_service.validator.SkillCandidateValidator;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final SkillCandidateValidator skillCandidateValidator;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        recommendationRequestValidator.validate(recommendationRequest);
        skillCandidateValidator.validate(recommendationRequest.getSkills());
        RecommendationRequest entity = recommendationRequestRepository.create(
                recommendationRequest.getRequesterId(),
                recommendationRequest.getReceiverId(),
                recommendationRequest.getMessage()
        );
        saveSkillRequests(recommendationRequest, entity);
        return recommendationRequestMapper.toDto(entity);
    }

    private void saveSkillRequests(RecommendationRequestDto recommendationRequest, RecommendationRequest entity) {
        recommendationRequest.getSkills()
                .forEach(skill -> {
                    SkillRequest skillRequest = skillRequestRepository.create(entity.getId(), skill.getId());
                    entity.addSkillRequest(skillRequest);
                });
    }
}
