package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {

    private static final int MAX_USERS_SHARING_GOAL = 10;
    private final UserService userService;
    private final GoalService goalService;

    public void validateInvitation(GoalInvitationDto invitation) {
        goalService.findGoalById(invitation.getGoalId());
        if (invitation.getInviterId() == null) {
            throw new DataValidationException("Inviter id is required");
        }
        if (invitation.getInvitedUserId() == null) {
            throw new DataValidationException("Invited user id is required");
        }
        if (invitation.getInvitedUserId().equals(invitation.getInviterId())) {
            throw new DataValidationException("Inviter and invited user cannot be the same person");
        }
        if (!userService.existsById(invitation.getInviterId())) {
            throw new DataValidationException("Inviter does not exist");
        }
        if (!userService.existsById(invitation.getInvitedUserId())) {
            throw new DataValidationException("Invited user with " + invitation.getInvitedUserId() + " does not exist");
        }
        if (goalService.countUsersSharingGoal(invitation.getGoalId()) > MAX_USERS_SHARING_GOAL) {
            throw new DataValidationException("Only 10 users can share a goal at a time");
        }
    }

    public void validate(GoalInvitation invitation) {
        List<User> users = goalService.findUsersByGoalId(invitation.getGoalId());
        goalService.findGoalById(invitation.getGoalId());
        if (users.contains(invitation.getInvited())) {
            throw new DataValidationException("User with id " + invitation.getInvited() + " already shares this goal");
        }
    }
}