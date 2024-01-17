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
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
        when(userService.findUserById(invitationDto.getInviterId())).thenReturn(Optional.of(inviter));
        when(userService.findUserById(invitationDto.getInvitedUserId())).thenReturn(Optional.of(invited));
        when(userService.existsUserById(inviter.getId())).thenReturn(true);

        var result = goalInvitationService.createInvitation(invitationDto);

        verify(goalInvitationRepository, times(1)).save(goalInvitation);

        assertEquals(invitationDto, invitationMapper.toDto(invitationMapper.toEntity(result)));
    }

    @Test
    @DisplayName("Test inviter does not exist")
    public void testCreateInvitationWithInviterNotFound() {
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(3L);

        User inviter = new User();
        inviter.setId(1L);

        when(userService.findUserById(invitationDto.getInviterId())).thenReturn(Optional.of(inviter));

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.createInvitation(invitationDto));

        verify(goalInvitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test invited does not exist")
    public void testCreateInvitationWithInvitedNotFound() {
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(3L);

        User invited = new User();
        invited.setId(1L);

        when(userService.findUserById(invitationDto.getInviterId())).thenReturn(Optional.of(invited));

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.createInvitation(invitationDto));

        verify(goalInvitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test users is the same")
    public void testCreateInvitationWithUsersIsTheSame() {
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(1L);

        User inviter = new User();
        inviter.setId(1L);

        User invited = new User();
        inviter.setId(1L);

        when(userService.findUserById(invitationDto.getInviterId())).thenReturn(Optional.of(inviter));
        when(userService.findUserById(invitationDto.getInvitedUserId())).thenReturn(Optional.of(invited));

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.createInvitation(invitationDto));

        verify(goalInvitationRepository, never()).save(any());
    }
}
