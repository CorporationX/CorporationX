package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    private static final int MAX_ACTIVE_GOALS_SIMULTANEOUSLY = 3;
    private final SkillService skillService;
    private final UserService userService;
    private final GoalRepository goalRepository;

    public void validateBeforeUpdate(Goal goal, GoalDto updatedGoal) {
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new DataValidationException(ErrorMessage.GOAL_ALREADY_COMPLETED);
        }
        areExistingSkills(updatedGoal);
    }

    public void validateBeforeCreate(long userId, GoalDto goal) {
        areExistingSkills(goal);
        int currentGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
        if (currentGoalsCount == MAX_ACTIVE_GOALS_SIMULTANEOUSLY) {
            throw new DataValidationException(ErrorMessage.TOO_MANY_GOALS, MAX_ACTIVE_GOALS_SIMULTANEOUSLY);
        }
    }

    public void validate(long mentorId, long menteeId, GoalDto goal) {
        validateBeforeCreate(menteeId, goal);
        if (!userService.areOwnedSkills(mentorId, goal.getSkillIds())) {
            throw new DataValidationException(ErrorMessage.INVALID_USER_SKILLS);
        }
    }

    private void areExistingSkills(GoalDto goal) {
        if (!skillService.areExistingSkills(goal.getSkillIds())) {
            throw new DataValidationException("Invalid skills offered within a goal");
        }
    }
}