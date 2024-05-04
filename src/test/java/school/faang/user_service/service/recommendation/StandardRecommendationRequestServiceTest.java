package school.faang.user_service.service.recommendation;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestFilter;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestFilterStorage;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StandardRecommendationRequestServiceTest {
    private static final long RECOMMENDATION_REQUEST_ID = 1L;
    private static final long SKILL_ID = 1L;
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final String REQUEST_MESSAGE = "msg";
    private static final LocalDateTime CREATED_DATE_TIME = LocalDateTime.now().minusMonths(12);
    private static final LocalDateTime UPDATED_DATE_TIME = LocalDateTime.now().plusMonths(7);
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    @InjectMocks
    private StandardRecommendationRequestService recommendationRequestService;
    private RecommendationRequest recommendationRequest;
    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequestFilterDto requestFilterDto;

    @BeforeEach
    public void setUp() {
        Skill skill = new Skill();
        skill.setId(SKILL_ID);
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setSkill(skill);
        User requester = new User();
        requester.setId(REQUESTER_ID);
        User receiver = new User();
        receiver.setId(RECEIVER_ID);
        recommendationRequest = RecommendationRequest.builder()
                .id(RECOMMENDATION_REQUEST_ID)
                .message(REQUEST_MESSAGE)
                .status(RequestStatus.ACCEPTED)
                .skills(List.of(skillRequest))
                .requester(requester)
                .receiver(receiver)
                .createdAt(CREATED_DATE_TIME)
                .updatedAt(UPDATED_DATE_TIME)
                .build();
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(recommendationRequest.getId())
                .message(recommendationRequest.getMessage())
                .status(RequestStatus.ACCEPTED)
                .skillIds(List.of(skill.getId()))
                .requesterId(requester.getId())
                .receiverId(receiver.getId())
                .createdAt(recommendationRequest.getCreatedAt())
                .updatedAt(recommendationRequest.getUpdatedAt())
                .build();
        requestFilterDto = new RecommendationRequestFilterDto();
    }

    @Test
    public void whenRequestRecommendationAndUserIsNotExistsThenThrowsException() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(false);
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequest));
    }

    @Test
    public void whenRequestRecommendationAndSkillIsNotExistsThenThrowsException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.existsById(SKILL_ID)).thenReturn(false);
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequest));
    }

    @Test
    public void whenRequestRecommendationAndCreationDateBeforeSixMonthThenThrowsException() {
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusDays(4));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequest));
    }

    @Test
    public void whenRequestRecommendationSuccessfully() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);
        RecommendationRequestDto actual = recommendationRequestService.requestRecommendation(recommendationRequest);
        assertThat(actual).isEqualTo(recommendationRequestDto);
    }

    @Test
    public void whenGetRecommendationRequestsAndNoApplicableFiltersThenReturnEmptyList() {
        recommendationRequestService = new StandardRecommendationRequestService(recommendationRequestRepository,
                userRepository, skillRepository, recommendationRequestMapper, getFilters());
        requestFilterDto.setMessage(REQUEST_MESSAGE + SKILL_ID);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        List<RecommendationRequestDto> filteredRequests = recommendationRequestService.getRecommendationRequests(requestFilterDto);
        assertThat(filteredRequests).isEmpty();
    }

    @Test
    public void whenGetRecommendationRequestsWithFilterByMessageThenReturnResultList() {
        recommendationRequestService = new StandardRecommendationRequestService(recommendationRequestRepository,
                userRepository, skillRepository, recommendationRequestMapper, getFilters());
        requestFilterDto.setMessage(REQUEST_MESSAGE);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        List<RecommendationRequestDto> filteredRequests = recommendationRequestService.getRecommendationRequests(requestFilterDto);
        assertThat(filteredRequests).isEqualTo(List.of(recommendationRequestDto));
    }

    @Test
    public void whenGetRecommendationRequestsWithFilterByStatusThenReturnResultList() {
        recommendationRequestService = new StandardRecommendationRequestService(recommendationRequestRepository,
                userRepository, skillRepository, recommendationRequestMapper, getFilters());
        requestFilterDto.setStatus(RequestStatus.ACCEPTED);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        List<RecommendationRequestDto> filteredRequests = recommendationRequestService.getRecommendationRequests(requestFilterDto);
        assertThat(filteredRequests).isEqualTo(List.of(recommendationRequestDto));
    }

    @Test
    public void whenGetRecommendationRequestsWithFilterBySkillIdsThenReturnResultList() {
        recommendationRequestService = new StandardRecommendationRequestService(recommendationRequestRepository,
                userRepository, skillRepository, recommendationRequestMapper, getFilters());
        requestFilterDto.setSkillIds(List.of(SKILL_ID));
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        List<RecommendationRequestDto> filteredRequests = recommendationRequestService.getRecommendationRequests(requestFilterDto);
        assertThat(filteredRequests).isEqualTo(List.of(recommendationRequestDto));
    }

    @Test
    public void whenGetRecommendationRequestsWithFilterByRequesterIdThenReturnResultList() {
        recommendationRequestService = new StandardRecommendationRequestService(recommendationRequestRepository,
                userRepository, skillRepository, recommendationRequestMapper, getFilters());
        requestFilterDto.setRequesterId(REQUESTER_ID);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        List<RecommendationRequestDto> filteredRequests = recommendationRequestService.getRecommendationRequests(requestFilterDto);
        assertThat(filteredRequests).isEqualTo(List.of(recommendationRequestDto));
    }

    @Test
    public void whenGetRecommendationRequestsWithFilterByReceiverIdThenReturnResultList() {
        recommendationRequestService = new StandardRecommendationRequestService(recommendationRequestRepository,
                userRepository, skillRepository, recommendationRequestMapper, getFilters());
        requestFilterDto.setReceiverId(RECEIVER_ID);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        List<RecommendationRequestDto> filteredRequests = recommendationRequestService.getRecommendationRequests(requestFilterDto);
        assertThat(filteredRequests).isEqualTo(List.of(recommendationRequestDto));
    }

    @Test
    public void whenGetRecommendationRequestsWithFilterByCreatedTimeThenReturnResultList() {
        recommendationRequestService = new StandardRecommendationRequestService(recommendationRequestRepository,
                userRepository, skillRepository, recommendationRequestMapper, getFilters());
        requestFilterDto.setCreatedAt(CREATED_DATE_TIME);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        List<RecommendationRequestDto> filteredRequests = recommendationRequestService.getRecommendationRequests(requestFilterDto);
        assertThat(filteredRequests).isEqualTo(List.of(recommendationRequestDto));
    }

    @Test
    public void whenGetRecommendationRequestsWithFilterByUpdatedTimeThenReturnResultList() {
        recommendationRequestService = new StandardRecommendationRequestService(recommendationRequestRepository,
                userRepository, skillRepository, recommendationRequestMapper, getFilters());
        requestFilterDto.setUpdatedAt(UPDATED_DATE_TIME);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        List<RecommendationRequestDto> filteredRequests = recommendationRequestService.getRecommendationRequests(requestFilterDto);
        assertThat(filteredRequests).isEqualTo(List.of(recommendationRequestDto));
    }

    @Test
    public void whenGetRecommendationRequestByIdAndRecommendationRequestISNotExistsThenThrowsException() {
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationRequestService.getRequest(1L));
    }

    @Test
    public void whenGetRecommendationRequestByIdSuccessfully() {
        when(recommendationRequestRepository.findById(RECOMMENDATION_REQUEST_ID)).thenReturn(Optional.of(recommendationRequest));
        RecommendationRequestDto actual = recommendationRequestService.getRequest(RECOMMENDATION_REQUEST_ID);
        assertThat(actual).isEqualTo(recommendationRequestDto);
    }

    private List<RecommendationRequestFilter> getFilters() {
        return RecommendationRequestFilterStorage.getStorage();
    }
}