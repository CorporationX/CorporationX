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
import school.faang.user_service.exception.goal.DataValidationException;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
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
    private GoalInvitationValidator goalInvitationValidator;
    private UserService userService;
    private GoalInvitationService goalInvitationService;

    @BeforeEach
    public void init() {
        goalInvitationRepository = mock(GoalInvitationRepository.class);
        goalInvitationMapper = mock(GoalInvitationMapper.class);
        goalInvitationValidator = new GoalInvitationValidator();
        userService = mock(UserService.class);
        goalInvitationService = new GoalInvitationService(goalInvitationRepository,
                goalInvitationMapper, goalInvitationValidator, userService);
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
    @DisplayName("Test accept invitation when data is valid")
    public void testAcceptGoalInvitationWhenDataIsValid() {
        long invitationId = 1L;

        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setId(invitationId);

        GoalInvitation currentInvitation = new GoalInvitation();
        currentInvitation.setId(2L);

        Goal currentGoal = new Goal();
        currentGoal.setId(2L);

        List<GoalInvitation> currentUserReceivedInvitations = new ArrayList<>();
        currentUserReceivedInvitations.add(currentInvitation);

        List<Goal> currentUserGoals = new ArrayList<>();
        currentUserGoals.add(currentGoal);

        User invitedUser = new User();
        invitedUser.setId(1L);
        invitedUser.setReceivedGoalInvitations(currentUserReceivedInvitations);
        invitedUser.setGoals(currentUserGoals);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);
        goalInvitation.setInvited(invitedUser);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));
        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(invitationDto);

        GoalInvitationDto result = goalInvitationService.acceptGoalInvitation(invitationId);

        assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
        assertEquals(currentUserReceivedInvitations, invitedUser.getReceivedGoalInvitations());
        assertEquals(currentUserGoals, invitedUser.getGoals());
        verify(userService, times(1)).saveUser(invitedUser);
        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        assertEquals(invitationDto, result);
    }

    @Test
    @DisplayName("Test accept invitation when data is invalid")
    public void testAcceptGoalInvitationWhenDataIsInvalid() {
        long invitationId = 1L;

        GoalInvitation currentInvitation = new GoalInvitation();
        currentInvitation.setId(2L);

        Goal currentGoal = new Goal();
        currentGoal.setId(2L);

        List<GoalInvitation> currentUserReceivedInvitations = new ArrayList<>();
        currentUserReceivedInvitations.add(currentInvitation);

        List<Goal> currentUserGoals = new ArrayList<>();
        currentUserGoals.add(currentGoal);

        User invitedUser = new User();
        invitedUser.setId(1L);
        invitedUser.setReceivedGoalInvitations(currentUserReceivedInvitations);
        invitedUser.setGoals(currentUserGoals);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);
        goalInvitation.setInvited(invitedUser);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.acceptGoalInvitation(invitationId));
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

        GoalInvitation currentInvitation = new GoalInvitation();
        currentInvitation.setId(2L);

        List<GoalInvitation> currentUserReceivedInvitations = new ArrayList<>();
        currentUserReceivedInvitations.add(currentInvitation);

        List<Goal> currentUserGoals = new ArrayList<>();
        currentUserGoals.add(goal1);
        currentUserGoals.add(goal2);
        currentUserGoals.add(goal3);

        User invitedUser = new User();
        invitedUser.setId(1L);
        invitedUser.setReceivedGoalInvitations(currentUserReceivedInvitations);
        invitedUser.setGoals(currentUserGoals);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(id);
        goalInvitation.setInvited(invitedUser);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(id)).thenReturn(Optional.of(goalInvitation));

        assertThrows(DataValidationException.class, () -> goalInvitationService.acceptGoalInvitation(id));
    }
}
