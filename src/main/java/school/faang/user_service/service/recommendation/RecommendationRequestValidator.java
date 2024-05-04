package school.faang.user_service.service.recommendation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
@NoArgsConstructor
class RecommendationRequestValidator {
    private UserRepository userRepository;
    private SkillRepository skillRepository;


    public boolean validateRecommendationRequest(RecommendationRequest recommendationRequest) {
        long requesterId = recommendationRequest.getRequester().getId();
        long receiverId = recommendationRequest.getReceiver().getId();
        List<SkillRequest> skillRequests = recommendationRequest.getSkills();
        checkUsersExisting(requesterId);
        checkUsersExisting(receiverId);
        checkSkillsExisting(skillRequests);
        checkRequestDate(recommendationRequest);
        return true;
    }

    private void checkUsersExisting(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(String.format("user with id: %d is not exists", userId));
        }
    }

    private void checkSkillsExisting(List<SkillRequest> skillRequests) {
        skillRequests.forEach(skillRequest -> {
            long skillId = skillRequest.getSkill().getId();
            if (!skillRepository.existsById(skillId)) {
                throw new NoSuchElementException(String.format("skill with id: %d is not exists", skillId));
            }
        });
    }

    private void checkRequestDate(RecommendationRequest recommendationRequest) {
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