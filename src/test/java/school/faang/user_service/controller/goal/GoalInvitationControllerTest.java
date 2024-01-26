package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.ArrayList;
import java.util.List;

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
    @DisplayName("Test create invitation")
    void testCreateInvitation() {
        GoalInvitationDto dto = new GoalInvitationDto();

        goalInvitationController.createInvitation(dto);

        verify(goalInvitationService, times(1)).createInvitation(dto);
    }

    @Test
    public void testRejectGoalInvitation() {
        long invitationId = 1L;

        GoalInvitationDto expected = new GoalInvitationDto();
        expected.setId(invitationId);

        when(goalInvitationService.rejectGoalInvitation(anyLong())).thenReturn(expected);

        GoalInvitationDto actual = goalInvitationController.rejectGoalInvitation(invitationId);

        assertEquals(expected, actual);
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
