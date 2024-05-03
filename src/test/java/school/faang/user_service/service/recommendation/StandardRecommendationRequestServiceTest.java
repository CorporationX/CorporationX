package school.faang.user_service.service.recommendation;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StandardRecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @InjectMocks
    private StandardRecommendationRequestService recommendationRequestService;
    private RecommendationRequest recommendationRequest;
    private RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    public void setUp() {
        Skill skill = new Skill();
        skill.setId(1L);
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setSkill(skill);
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .message("msg")
                .skills(List.of(skillRequest))
                .requester(requester)
                .receiver(receiver)
                .createdAt(LocalDateTime.now().minusMonths(12))
                .build();
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(recommendationRequest.getId())
                .message(recommendationRequest.getMessage())
                .skillIds(List.of(skill.getId()))
                .requesterId(requester.getId())
                .receiverId(receiver.getId())
                .createdAt(recommendationRequest.getCreatedAt())
                .build();
    }

    @Test
    public void whenRequestRecommendationAndUserIsNotExistsThenThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationRequestService.requestRecommendation(recommendationRequest));
    }

    @Test
    public void whenRequestRecommendationAndSkillIsNotExistsThenThrowsException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(false);
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
        when(recommendationRequestMapper.fromEntityToDto(recommendationRequest)).thenReturn(recommendationRequestDto);
        RecommendationRequestDto actual = recommendationRequestService.requestRecommendation(recommendationRequest);
        assertThat(actual).isEqualTo(recommendationRequestDto);
    }
}