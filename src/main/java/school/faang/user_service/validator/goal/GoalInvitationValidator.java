package school.faang.user_service.validator.goal;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.DataValidationException;
import school.faang.user_service.exception.goal.EntityNotFoundException;

/**
 * @author Alexander Bulgakov
 */
@Component
public class GoalInvitationValidator {
    private static final int MAX_ACTIVE_GOALS = 3;
    @SneakyThrows
    public boolean checkUser(User inviter, User invited) {
        if (inviter.getId() == invited.getId()) {
            throw new EntityNotFoundException("This users is the same");
        } else {
            return true;
        }
    }

    @SneakyThrows
    public boolean checkData(User user, GoalInvitation goalInvitation) {
        if (goalInvitation.getStatus() == RequestStatus.ACCEPTED) {
            throw new EntityNotFoundException("GoalInvitation is not found");
        } else if (user.getGoals().contains(goalInvitation.getGoal())) {
            throw new DataValidationException("User already exist this goal");
        } else if (!(user.getGoals().size() < MAX_ACTIVE_GOALS)) {
            throw new DataValidationException("User already have maximum active goals");
        } else {
            return true;
        }
    }
}
