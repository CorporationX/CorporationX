package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.DataValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {
    @InjectMocks
    private GoalInvitationValidator goalInvitationValidator;

    @Test
    public void testCheckData_WhenUserAlreadyHasMaxActiveGoals_ShouldThrowDataValidationException() {
        Goal goal1 = new Goal();
        goal1.setId(1L);
        Goal goal2 = new Goal();
        goal2.setId(2L);
        Goal goal3 = new Goal();
        goal3.setId(3L);

        List<Goal> userGoals = List.of(goal1, goal2, goal3);

        User user = new User();
        user.setId(1L);
        user.setGoals(userGoals);

        Goal goal4 = new Goal();
        goal4.setId(4L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setGoal(goal4);
        goalInvitation.setStatus(RequestStatus.PENDING);


        assertThrows(DataValidationException.class, () -> {
            goalInvitationValidator.validateGoal(user, goalInvitation);
        });
    }
}
