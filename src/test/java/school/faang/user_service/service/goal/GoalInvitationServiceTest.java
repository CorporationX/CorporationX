package school.faang.user_service.service.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalInvitationValidator goalInvitationValidator;
    @Mock
    private UserService userService;
    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    @DisplayName("Test getGoalInvitation when goal invitation exists")
    public void testGetGoalInvitationWhenExists() {
        long invitationId = 1L;
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(java.util.Optional.of(goalInvitation));

        GoalInvitation result = goalInvitationService.getGoalInvitation(invitationId);

        assertNotNull(result);
        assertEquals(invitationId, result.getId());
    }

    @Test
    @DisplayName("Test getGoalInvitation when goal invitation does not exist")
    public void testGetGoalInvitationWhenNotExists() {
        long invitationId = 1L;

        when(goalInvitationRepository.findById(invitationId)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.getGoalInvitation(invitationId));
    }

    @Test
    @DisplayName("Test acceptGoalInvitation when data is valid")
    public void testAcceptGoalWhenInvitationDataIsValid() {
        long invitationId = 1L;

        Goal goal = new Goal();
        goal.setId(1L);

        List<Goal> goals = List.of(goal);

        User invited = new User();
        invited.setId(1L);
        invited.setGoals(goals);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);

        List<GoalInvitation> invitations = List.of(goalInvitation);

        invited.setReceivedGoalInvitations(invitations);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(java.util.Optional.of(goalInvitation));
        when(goalInvitationValidator.checkData(invited, goalInvitation)).thenReturn(true);

        goalInvitationService.acceptGoalInvitation(invitationId);

        assertTrue(invited.getReceivedGoalInvitations().contains(goalInvitation));
        assertTrue(invited.getGoals().contains(goal));
        assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
    }

    @Test
    @DisplayName("Test acceptGoalInvitation when data is invalid")
    public void testAcceptGoalInvitationInvalidData() {
        long invitationId = 1L;

        User invitedUser = new User();
        invitedUser.setId(1L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);
        goalInvitation.setInvited(invitedUser);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(java.util.Optional.of(goalInvitation));
        when(goalInvitationValidator.checkData(invitedUser, goalInvitation)).thenReturn(false);

        goalInvitationService.acceptGoalInvitation(invitationId);

        assertFalse(invitedUser.getReceivedGoalInvitations().contains(goalInvitation));
        assertFalse(invitedUser.getGoals().contains(goalInvitation.getGoal()));
        assertNotEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
    }
}
