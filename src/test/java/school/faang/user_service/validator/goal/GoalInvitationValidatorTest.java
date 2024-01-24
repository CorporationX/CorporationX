package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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