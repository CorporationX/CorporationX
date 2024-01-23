package school.faang.user_service.validator.goal;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.goal.EntityNotFoundException;

/**
 * @author Alexander Bulgakov
 */
@Component
public class GoalInvitationValidator {
    @SneakyThrows
    public void checkUser(long inviterId, long invitedId) {
        if (inviterId == invitedId) {
            throw new EntityNotFoundException("This users is the same");
        }
    }
}
