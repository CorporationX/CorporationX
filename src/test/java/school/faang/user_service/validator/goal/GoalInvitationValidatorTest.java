package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {
    private UserService userService;
    private GoalInvitationValidator goalInvitationValidator;

    @BeforeEach
    public void setup() {
        userService = mock(UserService.class);
        goalInvitationValidator = new GoalInvitationValidator(userService);
    }

    @Test
    public void testCheckUser_InviterAndInvitedAreSame() {
        long inviterId = 1L;
        long invitedId = 1L;

        assertThrows(EntityNotFoundException.class, () ->
            goalInvitationValidator.checkUser(inviterId, invitedId)
        );
    }

    @Test
    public void testCheckUser_InviterIdNotEqualsInvitedId_NoExceptionThrown() {
        long inviterId = 1L;
        long invitedId = 2L;

        Assertions.assertDoesNotThrow(() -> {
            goalInvitationValidator.checkUser(inviterId, invitedId);
        });
    }

    @Test
    public void testCheckUser_InviterIdNotExist_EntityNotFoundExceptionThrown() {
        long inviterId = 1L;
        long invitedId = 2L;

        when(userService.existsUserById(invitedId)).thenReturn(true);
        when(userService.existsUserById(inviterId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
            goalInvitationValidator.checkUser(inviterId, invitedId));
    }

    @Test
    public void testCheckUser_InvitedIdNotExist_EntityNotFoundExceptionThrown() {
        long inviterId = 1L;
        long invitedId = 2L;

        when(userService.existsUserById(inviterId)).thenReturn(true);

        assertThrows(EntityNotFoundException.class, () ->
            goalInvitationValidator.checkUser(inviterId, invitedId));
    }
}
