package school.faang.user_service.controller.goal;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class GoalInvitationControllerTest {
    private MockMvc mockMvc;
    @Mock
    private GoalInvitationService goalInvitationService;
    @InjectMocks
    private GoalInvitationController goalInvitationController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalInvitationController).build();
    }

    @SneakyThrows
    @Test
    public void testRejectGoalInvitation() {
        long invitationId = 1L;

        GoalInvitationDto expected = new GoalInvitationDto();
        expected.setId(invitationId);

        when(goalInvitationService.rejectGoalInvitation(anyLong())).thenReturn(expected);

        GoalInvitationDto actual = goalInvitationController.rejectGoalInvitation(invitationId);

        assertEquals(expected, actual);
    }
}
