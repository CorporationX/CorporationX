package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.DataValidationException;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.dto.goal.InvitationFilterDto;

/**
 * @author Alexander Bulgakov
 */
@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {
    private static final int MAX_ACTIVE_GOALS = 3;
    private final UserService userService;

    public void checkUser(long inviterId, long invitedId) {
        if (userService.existsUserById(inviterId)) {
            throw new EntityNotFoundException("User by id: " + inviterId + " is not exist");
        } else if (userService.existsUserById(invitedId)) {
            throw new EntityNotFoundException("User by id: " + invitedId + " is not exist");
        } else if (inviterId == invitedId) {
            throw new EntityNotFoundException("This users is the same");
        }
    }

    public void validateGoal(User user, GoalInvitation goalInvitation) {
        if (goalInvitation.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("GoalInvitation is not active");
        } else if (user.getGoals().contains(goalInvitation.getGoal())) {
            throw new DataValidationException("User already exist this goal");
        } else if (user.getGoals().size() == MAX_ACTIVE_GOALS) {
            throw new DataValidationException("User already have maximum active goals");
        }
    }

    public boolean checkFilter(InvitationFilterDto filter) {
        return filter != null;
    }
}
