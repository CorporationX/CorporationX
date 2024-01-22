package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.goal.GoalInvitationService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class GoalInvitationControllerTest {
    @Mock
    private GoalInvitationService goalInvitationService;
    @InjectMocks
    private GoalInvitationController goalInvitationController;

    @Test
    public void testAcceptGoalInvitation() {
        goalInvitationController.acceptGoalInvitation(anyLong());

        verify(goalInvitationService, times(1)).acceptGoalInvitation(anyLong());
    }

    @Test
    @DisplayName("Test reject goal invitation")
    public void testRejectGoalInvitation() {
        goalInvitationController.rejectGoalInvitation(anyLong());

        verify(goalInvitationService, times(1)).rejectGoalInvitation(anyLong());
    }

}
