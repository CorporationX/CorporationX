package school.faang.user_service.service.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private GoalInvitationMapper invitationMapper;

    @Mock
    private GoalInvitationValidator goalInvitationValidator;

    @Mock
    private UserService userService;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    @DisplayName("Test create invitation")
    public void testCreateInvitation() {
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setId(1L);
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(3L);

        User inviter = new User();
        inviter.setId(1L);

        User invited = new User();
        invited.setId(3L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setInvited(invited);
        goalInvitation.setInviter(inviter);

        when(invitationMapper.toEntity(invitationDto)).thenReturn(goalInvitation);
        when(invitationMapper.toDto(goalInvitation)).thenReturn(invitationDto);
        when(userService.existsUserById(invitationDto.getInviterId())).thenReturn(true);
        when(userService.existsUserById(invitationDto.getInvitedUserId())).thenReturn(true);

        var result = goalInvitationService.createInvitation(invitationDto);

        verify(goalInvitationRepository, times(1)).save(goalInvitation);

        assertEquals(invitationDto, invitationMapper.toDto(invitationMapper.toEntity(result)));
    }
}
