package school.faang.user_service.service.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private GoalInvitationService goalInvitationService;


    @Test
    @DisplayName("Test accept goal invitation")
    public void testAcceptGoalInvitation_shouldAddInvitationToUserGoals() {
        long id = 1L;

        User invitedUser = new User();
        invitedUser.setId(1L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(id);
        goalInvitation.setInvited(invitedUser);

        Goal goal = new Goal();
        goal.setId(1L);

        List<Goal> goals = new ArrayList<>();
        goals.add(goal);

        invitedUser.setGoals(goals);

        when(goalInvitationRepository.findById(id)).thenReturn(Optional.of(goalInvitation));
        when(userRepository.existsById(invitedUser.getId())).thenReturn(true);
        when(goalInvitationRepository.existsById(goalInvitation.getId())).thenReturn(true);

        goalInvitationService.acceptGoalInvitation(id);

        assertTrue(invitedUser.getReceivedGoalInvitations().contains(goalInvitation));
        assertTrue(invitedUser.getGoals().contains(goalInvitation.getGoal()));
    }

    @Test
    @DisplayName("Test accept goal invitation when user not found")
    public void testAcceptGoalInvitation_shouldThrowExceptionWhenUserNotFound() {
        long invitationId = 1L;

        User invitedUser = new User();
        invitedUser.setId(1L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);
        goalInvitation.setInvited(invitedUser);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));
        when(userRepository.existsById(invitedUser.getId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> goalInvitationService.acceptGoalInvitation(invitationId));
    }

    @Test
    @DisplayName("Test accept goal invitation when goal invitation not found")
    public void testAcceptGoalInvitation_shouldThrowExceptionWhenGoalInvitationNotFound() {
        long invitationId = 1L;
        User invitedUser = new User();
        invitedUser.setId(1L);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> goalInvitationService.acceptGoalInvitation(invitationId));
    }

    @Test
    @DisplayName("Test reject goal invitation")
    public void testRejectGoalInvitation() {
        long invitationId = 1L;

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);

        Goal goal = new Goal();
        goal.setId(1L);
        goalInvitation.setGoal(goal);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(java.util.Optional.of(goalInvitation));
        when(goalRepository.existsById(goal.getId())).thenReturn(true);

        goalInvitationService.rejectGoalInvitation(invitationId);

        verify(goalInvitationRepository, times(1)).delete(goalInvitation);
    }
}
