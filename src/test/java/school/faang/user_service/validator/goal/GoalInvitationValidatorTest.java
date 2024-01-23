package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.goal.EntityNotFoundException;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {
    private GoalInvitationValidator validator;

    @BeforeEach
    public void setup() {
        validator = new GoalInvitationValidator();
    }

    @Test
    public void testCheckUser_InviterAndInvitedAreSame() {
        long inviterId = 1L;
        long invitedId = 1L;

        Assertions.assertThrows(EntityNotFoundException.class, () ->
            validator.checkUser(inviterId, invitedId)
        );
    }

    @Test
    public void testCheckUser_InviterIdNotEqualsInvitedId_NoExceptionThrown() {
        long inviterId = 1;
        long invitedId = 2;

        Assertions.assertDoesNotThrow(() -> {
            validator.checkUser(inviterId, invitedId);
        });
    }
}
