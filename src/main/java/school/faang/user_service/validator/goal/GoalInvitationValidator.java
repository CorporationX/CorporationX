package school.faang.user_service.validator.goal;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.goal.EntityNotFoundException;

/**
 * @author Alexander Bulgakov
 */
@Component
public class GoalInvitationValidator {
    @SneakyThrows
    public boolean checkUser(User inviter, User invited) {
        if (inviter.getId() == invited.getId()) {
            throw new EntityNotFoundException("This users is the same");
        } else {
            return true;
        }
    }
}
