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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.Optional;

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
    private UserRepository userRepository;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    @DisplayName("Test save Invitation")
    public void testCreateInvitation() {
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(3L);
        GoalInvitation goalInvitation = new GoalInvitation();
        User inviterUser = new User();
        inviterUser.setId(1L);
        User invitedUser = new User();
        invitedUser.setId(3L);

        when(invitationMapper.toEntity(invitationDto)).thenReturn(goalInvitation);
        when(userRepository.findById(invitationDto.getInviterId())).thenReturn(Optional.of(inviterUser));
        when(userRepository.findById(invitationDto.getInvitedUserId())).thenReturn(Optional.of(invitedUser));
        when(userRepository.existsById(inviterUser.getId())).thenReturn(true);
        when(userRepository.existsById(invitedUser.getId())).thenReturn(true);

        goalInvitationService.createInvitation(invitationDto);

        verify(goalInvitationRepository, times(1)).save(goalInvitation);
    }

    @Test
    @DisplayName("Test save Invitation with invalid input")
    void testCreateInvitationWithInvalidInput() {
        // Arrange
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(1L); // Same user ID as inviter
        User inviterUser = new User();
        inviterUser.setId(1L);

        when(userRepository.findById(invitationDto.getInviterId())).thenReturn(Optional.of(inviterUser));

        assertThrows(IllegalArgumentException.class, () -> goalInvitationService.createInvitation(invitationDto));
        verify(goalInvitationRepository, never()).save(any());
    }
}
