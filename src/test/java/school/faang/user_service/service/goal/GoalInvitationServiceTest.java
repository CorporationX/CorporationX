package school.faang.user_service.service.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.DataValidationException;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private GoalInvitationMapper goalInvitationMapper;
    @Mock
    private GoalService goalService;
    @Mock
    private UserService userService;
    @Mock
    private List<Filter<InvitationFilterDto, GoalInvitation>> filters;
    @InjectMocks
    private GoalInvitationService goalInvitationService;


    @Test
    @DisplayName("Test accept goal invitation")
    public void testAcceptGoalInvitation_shouldAddInvitationToUserGoals() {
        long id = 1L;

        User invitedUser = new User();
        invitedUser.setId(1L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(id);
        goalInvitation.setInvited(invitedUser);

        Goal goal = new Goal();
        goal.setId(1L);

        List<Goal> goals = new ArrayList<>();
        goals.add(goal);

        invitedUser.setGoals(goals);

        when(goalInvitationRepository.findById(id)).thenReturn(Optional.of(goalInvitation));
        when(userService.existsUserById(invitedUser.getId())).thenReturn(true);
        when(goalInvitationRepository.existsById(goalInvitation.getId())).thenReturn(true);

        goalInvitationService.acceptGoalInvitation(id);

        assertTrue(invitedUser.getReceivedGoalInvitations().contains(goalInvitation));
        assertTrue(invitedUser.getGoals().contains(goalInvitation.getGoal()));
    }

    @Test
    @DisplayName("Test accept goal invitation when user not found")
    public void testAcceptGoalInvitation_shouldThrowExceptionWhenUserNotFound() {
        long invitationId = 1L;

        User invitedUser = new User();
        invitedUser.setId(1L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);
        goalInvitation.setInvited(invitedUser);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));
        when(userService.existsUserById(invitedUser.getId())).thenReturn(false);
        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));
        when(userService.existsUserById(invitedUser.getId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.acceptGoalInvitation(invitationId));
    }

    @Test
    @DisplayName("Test accept goal invitation when goal invitation not found")
    public void testAcceptGoalInvitation_shouldThrowExceptionWhenGoalInvitationNotFound() {
        long invitationId = 1L;
        User invitedUser = new User();
        invitedUser.setId(1L);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.acceptGoalInvitation(invitationId));
    }

    @Test
    @DisplayName("Test accept goal invitation when goal invitation does not exists")
    public void testAcceptGoalInvitation_shouldThrowExceptionWhenGoalInvitationDoesNotExists() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
    }

    @Test
    @DisplayName("Test accept when users goals contain this goal")
    public void testAcceptGoalInvitationWhenUserGoalsContainsGoal() {
        long id = 1L;

        Goal goal = new Goal();
        goal.setId(1L);

        List<Goal> goals = List.of(goal);

        User invited = new User();
        invited.setId(1L);
        invited.setGoals(goals);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);

        when(goalInvitationRepository.findById(id)).thenReturn(Optional.of(goalInvitation));
        when(userService.existsUserById(invited.getId())).thenReturn(true);
        when(goalInvitationRepository.existsById(goalInvitation.getId())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> goalInvitationService.acceptGoalInvitation(id));
    }

    @Test
    @DisplayName("Test accept when users goals is full")
    public void testAcceptGoalInvitationWhenUserGoalsIsFull() {
        long id = 1L;

        Goal goal1 = new Goal();
        goal1.setId(1L);
        Goal goal2 = new Goal();
        goal1.setId(2L);
        Goal goal3 = new Goal();
        goal1.setId(3L);

        Goal addedGoal = new Goal();
        addedGoal.setId(4L);

        List<Goal> goals = List.of(goal1, goal2, goal3);

        User invited = new User();
        invited.setId(1L);
        invited.setGoals(goals);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(addedGoal);

        when(goalInvitationRepository.findById(id)).thenReturn(Optional.of(goalInvitation));
        when(userService.existsUserById(invited.getId())).thenReturn(true);
        when(goalInvitationRepository.existsById(goalInvitation.getId())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> goalInvitationService.acceptGoalInvitation(id));
    }

    @Test
    @DisplayName("Test reject goal invitation")
    public void testRejectGoalInvitation() {
        long invitationId = 1L;

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);

        Goal goal = new Goal();
        goal.setId(1L);
        goalInvitation.setGoal(goal);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(java.util.Optional.of(goalInvitation));
        when(goalService.existsGoalById(goal.getId())).thenReturn(true);

        goalInvitationService.rejectGoalInvitation(invitationId);

        verify(goalInvitationRepository, times(1)).delete(goalInvitation);
    }

//    @Test
//    public void testGetInvitations() {
//        InvitationFilterDto filter = new InvitationFilterDto();
//        filter.setInvitedNamePattern("Alexander");
//
//        User invited = new User();
//        invited.setId(1L);
//        invited.setUsername("Alexander Bulgakov");
//
//        GoalInvitation invitation = new GoalInvitation();
//        invitation.setId(1L);
//        invitation.setInvited(invited);
//
//        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
//        goalInvitationDto.setId(1L);
//        goalInvitationDto.setInvitedUserId(invited.getId());
//
//        List<GoalInvitation> goalInvitations = new ArrayList<>();
//        goalInvitations.add(invitation);
//
//        List<GoalInvitationDto> goalInvitationsDtoList = new ArrayList<>();
//        goalInvitationsDtoList.add(goalInvitationDto);
//
//        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);
//        when(goalInvitationMapper.toDto(goalInvitations.get(0))).thenReturn(new GoalInvitationDto());
//
//        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filter);
//
//        assertEquals(1, result.size());
//    }
}
