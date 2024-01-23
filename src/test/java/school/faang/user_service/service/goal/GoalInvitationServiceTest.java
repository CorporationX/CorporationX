package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    private GoalInvitationRepository goalInvitationRepository;
    private GoalInvitationMapper goalInvitationMapper;
    private UserService userService;
    private GoalService goalService;
    private GoalInvitationService goalInvitationService;

    @BeforeEach
    public void init() {
        goalInvitationRepository = mock(GoalInvitationRepository.class);
        goalInvitationMapper = mock(GoalInvitationMapper.class);
        userService = mock(UserService.class);
        goalService = mock(GoalService.class);

        goalInvitationService = new GoalInvitationService(goalInvitationRepository,
                goalInvitationMapper, userService, goalService);
    }

    @Test
    @DisplayName("Test getGoalInvitation when goal invitation exists")
    public void testGetGoalInvitationWhenExists() {
        long invitationId = 1L;
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(java.util.Optional.of(goalInvitation));

        GoalInvitation result = goalInvitationService.getGoalInvitationById(invitationId);

        assertNotNull(result);
        assertEquals(invitationId, result.getId());
    }

    @Test
    @DisplayName("Test getGoalInvitation when goal invitation does not exist")
    public void testGetGoalInvitationWhenNotExists() {
        long invitationId = 1L;

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.getGoalInvitationById(invitationId));
    }

    @Test
    void testRejectGoalInvitation() {
        long invitationId = 1L;

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);

        Goal goal = new Goal();
        goal.setId(1L);

        goalInvitation.setGoal(goal);

        User invited = new User();
        invited.setId(2L);

        goalInvitation.setInvited(invited);

        List<GoalInvitation> receivedGoalInvitations = new ArrayList<>();
        receivedGoalInvitations.add(goalInvitation);

        invited.setReceivedGoalInvitations(receivedGoalInvitations);

        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setId(invitationId);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(goalInvitationDto);

        GoalInvitationDto result = goalInvitationService.rejectGoalInvitation(invitationId);

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, goalInvitation.getStatus());
        assertTrue(invited.getReceivedGoalInvitations().isEmpty());
        verify(goalInvitationRepository, times(1)).findById(invitationId);
        verify(goalService, times(1)).existsGoalById(goal.getId());
        verify(userService, times(1)).saveUser(invited);
        verify(goalInvitationRepository, times(1)).save(goalInvitation);
    }

    @Test
    void testRejectGoalInvitation_InvitationNotFound() {
        long invitationId = 1L;

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.rejectGoalInvitation(invitationId));
        verify(goalInvitationRepository, times(1)).findById(invitationId);
        verify(goalService, never()).existsGoalById(anyLong());
        verify(userService, never()).saveUser(any(User.class));
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

}
