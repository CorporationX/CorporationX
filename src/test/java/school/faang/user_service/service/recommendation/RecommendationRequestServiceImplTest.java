package school.faang.user_service.service.recommendation;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceImplTest {
    private static final long RECOMMENDATION_REQUEST_ID = 1L;
    private static final long SKILL_ID = 1L;
    private static final String REJECTION_REASON = "reason";
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private RecommendationRequestFilter recommendationRequestFilter;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    private RecommendationRequestService recommendationRequestService;
    private RecommendationRequest recommendationRequest;
    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequestFilterDto requestFilterDto;
    private RejectionDto rejection;
    private List<RecommendationRequestFilter> filters;

    @BeforeEach
    public void setUp() {
        filters = List.of(recommendationRequestFilter);
        recommendationRequestService = new RecommendationRequestServiceImpl(recommendationRequestRepository, recommendationRequestValidator,
                recommendationRequestMapper, filters);
        Skill skill = new Skill();
        skill.setId(SKILL_ID);
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setSkill(skill);
        recommendationRequest = RecommendationRequest.builder()
                .id(RECOMMENDATION_REQUEST_ID)
                .skills(List.of(skillRequest))
                .build();
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(recommendationRequest.getId())
                .skillIds(List.of(skill.getId()))
                .build();
        requestFilterDto = new RecommendationRequestFilterDto();
        rejection = new RejectionDto();
    }

    @Test
    public void whenRequestRecommendationAndThrowsNoSuchElementException() {
        when(recommendationRequestValidator.validateRecommendationRequest(recommendationRequestDto)).thenThrow(NoSuchElementException.class);
        Assert.assertThrows(NoSuchElementException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequestDto));
    }

    @Test
    public void whenRequestRecommendationAndThrowsDataValidationException() {
        when(recommendationRequestValidator.validateRecommendationRequest(recommendationRequestDto)).thenThrow(DataValidationException.class);
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequestDto));
    }

    @Test
    public void whenRequestRecommendationSuccessfully() {
        when(recommendationRequestValidator.validateRecommendationRequest(recommendationRequestDto)).thenReturn(true);
        when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);
        when(recommendationRequestMapper.ToEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        RecommendationRequestDto actual = recommendationRequestService.requestRecommendation(recommendationRequestDto);
        assertThat(actual).isEqualTo(recommendationRequestDto);
    }

    @Test
    public void whenGetRecommendationRequestsAndNoApplicableFiltersThenReturnListWithoutFilters() {
        when(filters.get(0).isApplicable(requestFilterDto)).thenReturn(false);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        List<RecommendationRequestDto> filteredRequests = recommendationRequestService.getRecommendationRequests(requestFilterDto);
        assertThat(filteredRequests).isEqualTo(List.of(recommendationRequestDto));
    }

    @Test
    public void whenGetRecommendationRequestsWithFilterThenReturnFilteredList() {
        when(filters.get(0).isApplicable(requestFilterDto)).thenReturn(true);
        when(filters.get(0).filter(any(), any())).thenReturn(Stream.empty());
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        List<RecommendationRequestDto> filteredRequests = recommendationRequestService.getRecommendationRequests(requestFilterDto);
        assertThat(filteredRequests).isEmpty();
    }

    @Test
    public void whenGetRecommendationRequestByIdAndRecommendationRequestIsNotExistsThenThrowsException() {
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationRequestService.getRequest(RECOMMENDATION_REQUEST_ID));
    }

    @Test
    public void whenGetRecommendationRequestByIdSuccessfully() {
        when(recommendationRequestRepository.findById(RECOMMENDATION_REQUEST_ID)).thenReturn(Optional.of(recommendationRequest));
        RecommendationRequestDto actual = recommendationRequestService.getRequest(RECOMMENDATION_REQUEST_ID);
        assertThat(actual).isEqualTo(recommendationRequestDto);
    }

    @Test
    public void whenRejectRecommendationRequestAndRecommendationRequestIsNotExistsThenThrowsException() {
        rejection.setReason(REJECTION_REASON);
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationRequestService.rejectRequest(RECOMMENDATION_REQUEST_ID, rejection));
    }

    @Test
    public void whenRejectRecommendationRequestAndStatusAlreadyAcceptedThrowsException() {
        rejection.setReason(REJECTION_REASON);
        when(recommendationRequestRepository.findById(RECOMMENDATION_REQUEST_ID)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestValidator.checkRecommendationRequestStatus(any())).thenThrow(DataValidationException.class);
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationRequestService.rejectRequest(RECOMMENDATION_REQUEST_ID, rejection));
    }

    @Test
    public void whenRejectRecommendationRequestSuccessfully() {
        rejection.setReason(REJECTION_REASON);
        when(recommendationRequestValidator.checkRecommendationRequestStatus(any())).thenReturn(true);
        when(recommendationRequestRepository.findById(RECOMMENDATION_REQUEST_ID)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);
        RecommendationRequestDto actual = recommendationRequestService.rejectRequest(RECOMMENDATION_REQUEST_ID, rejection);
        assertThat(actual).isEqualTo(recommendationRequestDto);
    }
}