package school.faang.user_service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class RecommendationRequestValidator {
    private UserService userService;
    private SkillService skillService;


    public boolean validateRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        long requesterId = recommendationRequest.getReceiverId();
        long receiverId = recommendationRequest.getRequesterId();
        List<Long> skillIds = recommendationRequest.getSkillIds();
        checkUsersExisting(requesterId);
        checkUsersExisting(receiverId);
        checkSkillsExisting(skillIds);
        checkRequestDate(recommendationRequest);
        return true;
    }

    private void checkUsersExisting(Long userId) {
        userService.existsById(userId);
    }

    private void checkSkillsExisting(List<Long> skillIds) {
        skillIds.forEach(skillId -> skillService.existsById(skillId));
    }

    private void checkRequestDate(RecommendationRequestDto recommendationRequest) {
        LocalDateTime createdDateTime = recommendationRequest.getCreatedAt();
        LocalDateTime createdTimePlusSixMonth = createdDateTime.plusMonths(6);
        if (LocalDateTime.now().isBefore(createdTimePlusSixMonth)) {
            throw new DataValidationException(String.format("you can request this recommendation from %s", createdTimePlusSixMonth));
        }
    }

    public boolean checkRecommendationRequestStatus(RequestStatus status) {
        if (status == RequestStatus.ACCEPTED || status == RequestStatus.REJECTED) {
            throw new DataValidationException(String.format("recommendation request has already %s", status));
        }
        return true;
    }
}