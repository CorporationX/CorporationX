package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import school.faang.user_service.service.goal.GoalInvitationService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Alexander Bulgakov
 */

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {
@ExtendWith(MockitoExtension.class)
public class GoalInvitationControllerTest {
    @Mock
    private GoalInvitationService goalInvitationService;
    @InjectMocks
    private GoalInvitationController goalInvitationController;

    @Test
    @DisplayName("Test create invitation")
    void testCreateInvitation() {
        GoalInvitationDto dto = new GoalInvitationDto();

        goalInvitationController.createInvitation(dto);

        verify(goalInvitationService, times(1)).createInvitation(dto);
    }
}
    public void testAcceptGoalInvitation() {
        goalInvitationController.acceptGoalInvitation(anyLong());

        verify(goalInvitationService, times(1)).acceptGoalInvitation(anyLong());
    }

}
