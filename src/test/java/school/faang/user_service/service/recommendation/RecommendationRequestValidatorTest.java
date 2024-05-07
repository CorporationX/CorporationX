package school.faang.user_service.service.recommendation;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillServiceImpl;
import school.faang.user_service.service.user.UserServiceImpl;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestValidatorTest {
    private static final long RECOMMENDATION_REQUEST_ID = 1L;
    private static final long SKILL_ID = 1L;
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final LocalDateTime CREATED_DATE_TIME = LocalDateTime.now().minusMonths(12);

    @Mock
    private UserServiceImpl userService;
    @Mock
    private SkillServiceImpl skillService;
    @InjectMocks
    private RecommendationRequestValidator validator;
    private RecommendationRequestDto recommendationRequest;

    @BeforeEach
    public void setUp() {
        recommendationRequest = RecommendationRequestDto.builder()
                .id(RECOMMENDATION_REQUEST_ID)
                .status(RequestStatus.PENDING)
                .skillIds(List.of(SKILL_ID))
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .createdAt(CREATED_DATE_TIME)
                .build();
    }

    @Test
    void whenValidateRecommendationRequestAndUserIsNotExistThenThrowsException() {
        when(userService.existsById(anyLong())).thenThrow(NoSuchElementException.class);
        Assert.assertThrows(NoSuchElementException.class,
                () -> validator.validateRecommendationRequest(recommendationRequest));
    }

    @Test
    void whenValidateRecommendationRequestAndSkillIsNotExistThenThrowsException() {
        when(userService.existsById(anyLong())).thenReturn(true);
        when(skillService.existsById(SKILL_ID)).thenThrow(NoSuchElementException.class);
        Assert.assertThrows(NoSuchElementException.class,
                () -> validator.validateRecommendationRequest(recommendationRequest));
    }

    @Test
    void whenValidateRecommendationRequestAndCreatedDateIsNotValidThenThrowsException() {
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusDays(4));
        when(userService.existsById(anyLong())).thenReturn(true);
        when(skillService.existsById(anyLong())).thenReturn(true);
        Assert.assertThrows(DataValidationException.class,
                () -> validator.validateRecommendationRequest(recommendationRequest));
    }

    @Test
    void whenValidateRecommendationRequestSuccessfully() {
        when(userService.existsById(anyLong())).thenReturn(true);
        when(skillService.existsById(anyLong())).thenReturn(true);
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