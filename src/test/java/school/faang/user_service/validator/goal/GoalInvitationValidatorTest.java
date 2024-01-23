package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.DataValidationException;
import school.faang.user_service.exception.goal.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {
    @Mock
    private User inviter;

    @Mock
    private User invited;
    @InjectMocks
    private GoalInvitationValidator goalInvitationValidator;

    @Test
    public void testCheckUser_InviterAndInvitedAreSame() {
        long inviterId = 1L;
        long invitedId = 1L;

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                goalInvitationValidator.checkUser(inviterId, invitedId)
        );
    }

    @Test
    public void testCheckUser_InviterIdNotEqualsInvitedId_NoExceptionThrown() {
        long inviterId = 1;
        long invitedId = 2;

        Assertions.assertDoesNotThrow(() -> {
            goalInvitationValidator.checkUser(inviterId, invitedId);
        });
    }

    @Test
    public void testCheckData_WhenGoalInvitationIsPendingAndUserDoesNotHaveMaxActiveGoals_ShouldReturnTrue() {
        Goal userGoal = new Goal();
        userGoal.setId(2L);

        List<Goal> userGoals = new ArrayList<>();
        userGoals.add(userGoal);

        User user = new User();
        user.setId(1L);
        user.setGoals(userGoals);

        Goal goal = new Goal();
        goal.setId(1L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setGoal(goal);

        goalInvitation.setStatus(RequestStatus.PENDING);

        boolean result = goalInvitationValidator.checkData(user, goalInvitation);

        assertTrue(result);
    }

    @Test
    public void testCheckData_WhenGoalInvitationIsNotPending_ShouldThrowDataValidationException() {
        User user = new User();
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        assertThrows(DataValidationException.class, () -> {
            goalInvitationValidator.checkData(user, goalInvitation);
        });
    }

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
            goalInvitationValidator.checkData(user, goalInvitation);
        });
    }
}
