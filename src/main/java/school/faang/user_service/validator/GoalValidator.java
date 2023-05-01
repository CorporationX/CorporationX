package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Goal;
import school.faang.user_service.entity.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    private final SkillRepository skillRepository;

    public void validate(Goal goal, GoalDto updatedGoal) {
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new DataValidationException(ErrorMessage.GOAL_ALREADY_COMPLETED);
        } else if (goal.getStatus().equals(GoalStatus.ACTIVE) &&
                updatedGoal.getStatus().equals(GoalStatus.COMPLETED) &&
                !goal.getSkillsToAchieve().isEmpty()) {
            updatedGoal.getSkills().forEach(skill ->
                    skillRepository.assignSkillToUser(updatedGoal.getId(), updatedGoal.getUserId()));
        }
    }
}