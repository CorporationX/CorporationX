package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filter.recommendation.RequestFilter;
import school.faang.user_service.validator.RecommendationRequestValidator;
import school.faang.user_service.validator.SkillCandidateValidator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final SkillCandidateValidator skillCandidateValidator;
    private final List<RequestFilter> filters;

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

    @Transactional(readOnly = true)
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        Stream<RecommendationRequest> reqs = StreamSupport.stream(recommendationRequestRepository.findAll().spliterator(), false);
        filters.stream()
                .filter(candidate -> candidate.isApplicable(filter))
                .forEach(candidate -> candidate.applyFilter(reqs, filter));
        return reqs.skip((long) filter.getPageSize() * filter.getPage())
                .limit(filter.getPageSize())
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestRepository.findById(id)
                .map(recommendationRequestMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        ErrorMessage.RECOMMENDATION_REQUEST_NOT_FOUND.getMessage(),
                        id
                )));
    }

    private void saveSkillRequests(RecommendationRequestDto recommendationRequest, RecommendationRequest entity) {
        recommendationRequest.getSkills()
                .forEach(skill -> {
                    SkillRequest skillRequest = skillRequestRepository.create(entity.getId(), skill.getId());
                    entity.addSkillRequest(skillRequest);
                });
    }
}
