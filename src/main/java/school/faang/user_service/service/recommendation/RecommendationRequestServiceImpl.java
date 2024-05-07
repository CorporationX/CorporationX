package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestValidator recommendationValidator;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    @Override
    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        recommendationValidator.validateRecommendationRequest(recommendationRequest);
        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequestMapper.ToEntity(recommendationRequest));
        return recommendationRequestMapper.ToDto(savedRecommendationRequest);
    }

    @Override
    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto requestFilterDto) {
        List<RecommendationRequest> allRequests = recommendationRequestRepository.findAll();
        Stream<RecommendationRequest> requestStream = allRequests.stream();
        List<RecommendationRequestFilter> suitableFilters = recommendationRequestFilters.stream()
                .filter(requestFilter -> requestFilter.isApplicable(requestFilterDto))
                .toList();
        for (RecommendationRequestFilter requestFilter : suitableFilters) {
            requestStream = requestFilter.filter(requestStream, requestFilterDto);
        }
        return recommendationRequestMapper.ToDtoList(requestStream.toList());
    }

    @Override
    public RecommendationRequestDto getRequest(long recommendationRequestId) {
        return recommendationRequestMapper.ToDto(findById(recommendationRequestId));
    }

    @Override
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest request = findById(id);
        RecommendationRequestDto requestDto = recommendationRequestMapper.ToDto(request);
        recommendationValidator.validateRecommendationRequest(requestDto);
        recommendationValidator.checkRecommendationRequestStatus(request.getStatus());
        request.setRejectionReason(rejection.getReason());
        return recommendationRequestMapper.ToDto(recommendationRequestRepository.save(request));
    }

    @Override
    public RecommendationRequest findById(long id) {
        return  recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("recommendation request with id: %d is not exists", id)));
    }
}