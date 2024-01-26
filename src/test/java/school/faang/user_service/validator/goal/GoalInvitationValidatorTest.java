package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.DataValidationException;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testValidateGoal_WhenInvitationStatusIsNotPending_ShouldThrowDataValidationException() {
        GoalInvitation goalInvitation = new GoalInvitation();
        User user = goalInvitation.getInvited();
        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        assertThrows(DataValidationException.class, () -> {
            goalInvitationValidator.validateGoal(user, goalInvitation);
        });
    }

    @Test
    public void testValidateGoal_WhenUserAlreadyHasGoal_ShouldThrowDataValidationException() {
        Goal goal = new Goal();
        goal.setId(1L);

        List<Goal> userGoals = new ArrayList<>();
        userGoals.add(goal);

        User user = new User();
        user.setId(1L);
        user.setGoals(userGoals);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInvited(user);
        goalInvitation.setStatus(RequestStatus.PENDING);
        goalInvitation.setGoal(goal);

        assertThrows(DataValidationException.class, () -> {
            goalInvitationValidator.validateGoal(user, goalInvitation);
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
            goalInvitationValidator.validateGoal(user, goalInvitation);
        });
    }
}
