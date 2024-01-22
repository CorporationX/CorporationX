package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @DisplayName("Test accept invitation")
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

    @Test
    @DisplayName("Test get invitations with filter")
    public void testGetInvitations() {
        InvitationFilterDto filter = new InvitationFilterDto();
        filter.setInviterNamePattern("John");

        User inviter = new User();
        inviter.setId(1L);
        inviter.setUsername("John Daw");

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInviter(inviter);

        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setId(1L);
        goalInvitationDto.setInviterId(inviter.getId());

        List<GoalInvitationDto> expectedInvitations = new ArrayList<>();
        expectedInvitations.add(goalInvitationDto);

        when(goalInvitationService.getInvitations(filter)).thenReturn(expectedInvitations);

        var actual = goalInvitationController.getInvitations(filter);

        Assertions.assertEquals(actual, expectedInvitations);
    }
}
