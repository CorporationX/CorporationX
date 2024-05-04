package school.faang.user_service.service.recommendation;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestValidatorTest {
    private static final long RECOMMENDATION_REQUEST_ID = 1L;
    private static final LocalDateTime CREATED_DATE_TIME = LocalDateTime.now().minusMonths(12);

    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private RecommendationRequestValidator validator;
    private RecommendationRequest recommendationRequest;

    @BeforeEach
    public void setUp() {
        recommendationRequest = new RecommendationRequest();
        Skill skill = new Skill();
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setSkill(skill);
        User requester = new User();
        User receiver = new User();
        recommendationRequest = RecommendationRequest.builder()
                .id(RECOMMENDATION_REQUEST_ID)
                .status(RequestStatus.PENDING)
                .skills(List.of(skillRequest))
                .requester(requester)
                .receiver(receiver)
                .createdAt(CREATED_DATE_TIME)
                .build();
    }

    @Test
    void whenValidateRecommendationRequestAndUserIsNotExistThenThrowsException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        Assert.assertThrows(NoSuchElementException.class,
                () -> validator.validateRecommendationRequest(recommendationRequest));
    }

    @Test
    void whenValidateRecommendationRequestAndSkillIsNotExistThenThrowsException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.existsById(anyLong())).thenReturn(false);
        Assert.assertThrows(NoSuchElementException.class,
                () -> validator.validateRecommendationRequest(recommendationRequest));
    }

    @Test
    void whenValidateRecommendationRequestAndCreatedDateIsNotValidThenThrowsException() {
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusDays(4));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        Assert.assertThrows(DataValidationException.class,
                () -> validator.validateRecommendationRequest(recommendationRequest));
    }

    @Test
    void whenValidateRecommendationRequestSuccessfully() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        assertThat(validator.validateRecommendationRequest(recommendationRequest)).isTrue();
    }

    @Test
    void whenCheckRecommendationRequestStatusAndStatusAlreadyAcceptedThrowsException() {
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        Assert.assertThrows(DataValidationException.class,
                () -> validator.checkRecommendationRequestStatus(recommendationRequest.getStatus()));
    }

    @Test
    void whenCheckRecommendationRequestStatusAndStatusAlreadyRejectedThrowsException() {
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        Assert.assertThrows(DataValidationException.class,
                () -> validator.checkRecommendationRequestStatus(recommendationRequest.getStatus()));
    }

    @Test
    void whenCheckRecommendationRequestStatusSuccessfully() {
        assertThat(validator.checkRecommendationRequestStatus(recommendationRequest.getStatus())).isTrue();
    }
}