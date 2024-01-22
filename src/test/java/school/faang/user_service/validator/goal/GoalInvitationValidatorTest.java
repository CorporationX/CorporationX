package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.goal.EntityNotFoundException;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {
    private GoalInvitationValidator validator;
    private User inviter;
    private User invited;

    @BeforeEach
    public void setup() {
        validator = new GoalInvitationValidator();
        inviter = new User();
        invited = new User();
    }

    @Test
    public void testCheckUser_InviterAndInvitedAreSame() {
        inviter.setId(1L);
        invited.setId(1L);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            validator.checkUser(inviter, invited);
        });
    }

    @Test
    public void testCheckUser_InviterAndInvitedAreDifferent_ReturnsTrue() {
        inviter.setId(1L);
        invited.setId(2L);

        boolean result = validator.checkUser(inviter, invited);

        Assertions.assertTrue(result);
    }
}