package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.service.user.UserService;

/**
 * @author Alexander Bulgakov
 */
@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {
    private final UserService userService;
    @SneakyThrows
    public void checkUser(long inviterId, long invitedId) {
        if (userService.existsUserById(inviterId)) {
            throw new EntityNotFoundException("User by id: " + inviterId + " is not exist");
        } else if (userService.existsUserById(invitedId)) {
            throw new EntityNotFoundException("User by id: " + invitedId + " is not exist");
        } else if (inviterId == invitedId) {
            throw new EntityNotFoundException("This users is the same");
        }
    }
}
