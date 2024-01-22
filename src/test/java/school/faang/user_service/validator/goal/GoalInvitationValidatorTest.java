package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.goal.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @Test
    public void testCheckFilter_AllFieldsNull() {
        InvitationFilterDto filter = new InvitationFilterDto();

        boolean result = validator.checkFilter(filter);

        assertTrue(result);
    }

    @Test
    public void testCheckFilter_SomeFieldsNotNull() {
        InvitationFilterDto filter = new InvitationFilterDto();
        filter.setInviterNamePattern("John");
        filter.setInvitedId(1L);

        boolean result = validator.checkFilter(filter);

        assertFalse(result);
    }

}