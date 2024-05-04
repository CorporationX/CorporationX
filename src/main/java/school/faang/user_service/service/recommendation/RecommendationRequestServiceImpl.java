package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestFilter;

import java.util.ArrayList;
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
    public RecommendationRequestDto requestRecommendation(RecommendationRequest recommendationRequest) {
        recommendationValidator.validateRecommendationRequest(recommendationRequest);
        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        return recommendationRequestMapper.fromEntityToDto(savedRecommendationRequest);
    }

    @Override
    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto requestFilterDto) {
        List<RecommendationRequest> allRequests = new ArrayList<>(recommendationRequestRepository.findAll());
        Stream<RecommendationRequest> requestStream = allRequests.stream();
        List<RecommendationRequestFilter> suitableFilters = recommendationRequestFilters.stream()
                .filter(requestFilter -> requestFilter.isApplicable(requestFilterDto))
                .toList();
        for (RecommendationRequestFilter requestFilter : suitableFilters) {
            requestStream = requestFilter.filter(requestStream, requestFilterDto);
        }
        return recommendationRequestMapper.fromEntityListToDtoList(requestStream.toList());
    }

    @Override
    public RecommendationRequestDto getRequest(long recommendationRequestId) {
        return recommendationRequestMapper.fromEntityToDto(recommendationRequestRepository.findById(recommendationRequestId)
                .orElseThrow(() -> new DataValidationException(String.format("recommendation request with id: %d is not exists", recommendationRequestId))));
    }

    @Override
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("recommendation request with id: %d is not exists", id)));
        checkRejectionDto(rejection);
        recommendationValidator.validateRecommendationRequest(request);
        recommendationValidator.checkRecommendationRequestStatus(request.getStatus());
        request.setRejectionReason(rejection.getReason());
        return recommendationRequestMapper.fromEntityToDto(recommendationRequestRepository.save(request));
    }

    private void checkRejectionDto(RejectionDto rejection) {
        if (rejection.getReason() == null || rejection.getReason().isBlank()) {
            throw new DataValidationException("rejection should contain the reason");
        }
    }
}