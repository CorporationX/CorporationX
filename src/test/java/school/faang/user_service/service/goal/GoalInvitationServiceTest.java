package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
<<<<<<< HEAD
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
=======
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.DataValidationException;
>>>>>>> BJS2-1569
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

<<<<<<< HEAD
import static org.junit.jupiter.api.Assertions.assertEquals;
=======
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
>>>>>>> BJS2-1569
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
    private GoalInvitationService goalInvitationService;

    @BeforeEach
    public void init() {
        goalInvitationRepository = mock(GoalInvitationRepository.class);
        goalInvitationMapper = mock(GoalInvitationMapper.class);
        goalInvitationValidator = new GoalInvitationValidator();

        goalInvitationService = new GoalInvitationService(goalInvitationRepository,
                goalInvitationMapper, goalInvitationValidator);
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

        assertThrows(DataValidationException.class, () -> goalInvitationService.acceptGoalInvitation(invitationId));
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
        goalInvitationValidator = mock(GoalInvitationValidator.class);

        goalInvitationService = new GoalInvitationService(
                goalInvitationRepository, goalInvitationMapper, goalInvitationValidator);
    }

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

        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(invitationDto);

        var result = goalInvitationService.createInvitation(invitationDto);

        verify(goalInvitationRepository, times(1)).save(goalInvitation);

        assertEquals(invitationDto, goalInvitationMapper.toDto(goalInvitationMapper.toEntity(result)));
    }
}
