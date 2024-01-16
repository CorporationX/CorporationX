package school.faang.user_service.controller.goal;

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

        var expected = HttpStatus.OK;

        ResponseEntity<GoalInvitationDto> goalInvitationDtoResponseEntity = goalInvitationController.acceptGoalInvitation(anyLong());

        var actual = goalInvitationDtoResponseEntity.getStatusCode();

        assertEquals(expected, actual);
        verify(goalInvitationService, times(1)).acceptGoalInvitation(anyLong());
    }

}
