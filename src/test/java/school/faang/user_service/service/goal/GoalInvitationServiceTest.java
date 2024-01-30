package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.DataValidationException;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.impl.goalinvitation.InvitedIdFilter;
import school.faang.user_service.filter.impl.goalinvitation.InvitedNamePattern;
import school.faang.user_service.filter.impl.goalinvitation.InviterIdFilter;
import school.faang.user_service.filter.impl.goalinvitation.InviterNamePattern;
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
    private GoalInvitationValidator goalInvitationValidator;
    private UserService userService;
    private GoalService goalService;
    private List<Filter<InvitationFilterDto, GoalInvitation>> filters;
    private GoalInvitationService goalInvitationService;

    @BeforeEach
    public void init() {
        InviterNamePattern inviterNamePattern = new InviterNamePattern();
        InvitedNamePattern invitedNamePattern = new InvitedNamePattern();
        InvitedIdFilter invitedIdFilter = new InvitedIdFilter();
        InviterIdFilter inviterIdFilter = new InviterIdFilter();

        goalInvitationRepository = mock(GoalInvitationRepository.class);
        goalInvitationMapper = mock(GoalInvitationMapper.class);
        userService = mock(UserService.class);
        goalService = mock(GoalService.class);
        goalInvitationValidator = new GoalInvitationValidator();

        filters = List.of(inviterNamePattern, invitedNamePattern, invitedIdFilter, inviterIdFilter);

        goalInvitationService = new GoalInvitationService(goalInvitationRepository,
                goalInvitationMapper, goalInvitationValidator, userService, goalService, filters);
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

        assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.getGoalInvitationById(invitationId));
    }

    @Test
    @DisplayName("Test create invitation")
    public void testCreateInvitation() {
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setId(1L);
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(3L);
        invitationDto.setGoalId(1L);

        Goal goal = new Goal();
        goal.setId(1L);

        User inviter = new User();
        inviter.setId(1L);

        User invited = new User();
        invited.setId(3L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setInvited(invited);
        goalInvitation.setInviter(inviter);
        goalInvitation.setGoal(goal);
        goalInvitation.setStatus(RequestStatus.PENDING);

        GoalInvitationDto createdDto = new GoalInvitationDto();
        createdDto.setId(1L);
        createdDto.setInviterId(inviter.getId());
        createdDto.setInvitedUserId(invited.getId());
        createdDto.setGoalId(goal.getId());
        createdDto.setStatus(RequestStatus.PENDING);

        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(goalInvitation);
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(createdDto);

        var result = goalInvitationService.createInvitation(invitationDto);

        verify(goalInvitationRepository, times(1)).save(goalInvitation);

        assertEquals(createdDto, result);
    }

    @Test
    @DisplayName("Test accept invitation when data is valid")
    public void testAcceptGoalInvitationWhenDataIsValid() {
        long invitationId = 1L;

        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setId(invitationId);
        invitationDto.setInvitedUserId(1L);
        invitationDto.setStatus(RequestStatus.PENDING);

        Goal currentGoal = new Goal();
        currentGoal.setId(2L);

        List<Goal> currentUserGoals = new ArrayList<>();
        currentUserGoals.add(currentGoal);

        User invitedUser = new User();
        invitedUser.setId(1L);
        invitedUser.setGoals(currentUserGoals);

        GoalInvitation currentInvitation = new GoalInvitation();
        currentInvitation.setId(invitationId);
        currentInvitation.setInvited(invitedUser);
        currentInvitation.setStatus(RequestStatus.PENDING);

        GoalInvitation acceptedGoalInvitation = new GoalInvitation();
        acceptedGoalInvitation.setId(invitationId);
        acceptedGoalInvitation.setInvited(invitedUser);
        acceptedGoalInvitation.setStatus(RequestStatus.ACCEPTED);

        GoalInvitationDto acceptedDto = new GoalInvitationDto();
        acceptedDto.setId(invitationId);
        acceptedDto.setInvitedUserId(invitedUser.getId());
        acceptedDto.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(currentInvitation));
        when(goalInvitationRepository.save(currentInvitation)).thenReturn(acceptedGoalInvitation);
        when(goalInvitationMapper.toDto(currentInvitation)).thenReturn(acceptedDto);

        GoalInvitationDto result = goalInvitationService.acceptGoalInvitation(invitationId);

        assertEquals(RequestStatus.ACCEPTED, currentInvitation.getStatus());
        assertEquals(currentUserGoals, invitedUser.getGoals());
        verify(goalInvitationRepository, times(1)).save(currentInvitation);
        assertEquals(acceptedDto, result);
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
    }

    @Test
    void testRejectGoalInvitation() {
        long invitationId = 1L;

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(invitationId);

        Goal goal = new Goal();
        goal.setId(1L);

        goalInvitation.setGoal(goal);

        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setId(invitationId);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(goalInvitationDto);

        GoalInvitationDto result = goalInvitationService.rejectGoalInvitation(invitationId);

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, goalInvitation.getStatus());
        verify(goalInvitationRepository, times(1)).findById(invitationId);
        verify(goalService, times(1)).existsGoalById(goal.getId());
        verify(goalInvitationRepository, times(1)).save(goalInvitation);
    }

    @Test
    void testRejectGoalInvitation_InvitationNotFound() {
        long invitationId = 1L;

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.rejectGoalInvitation(invitationId));
        verify(goalInvitationRepository, times(1)).findById(invitationId);
        verify(goalService, never()).existsGoalById(anyLong());
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    @DisplayName("Test get invitations with filters")
    public void testGetInvitations() {
        InvitationFilterDto filter = new InvitationFilterDto();
        filter.setInviterNamePattern("Alex");
        filter.setInvitedNamePattern("Alex");
        filter.setInvitedId(2L);

        User inviter1 = new User();
        inviter1.setId(1L);
        inviter1.setUsername("Alexander Bulgakov");

        User invited1 = new User();
        invited1.setId(2L);
        invited1.setUsername("Alexey Goloverdin");

        User invited2 = new User();
        invited2.setId(4L);
        invited2.setUsername("Anna");

        User inviter2 = new User();
        inviter2.setId(3L);
        inviter2.setUsername("Pavel");

        GoalInvitation invitation1 = new GoalInvitation();
        invitation1.setId(1L);
        invitation1.setInviter(inviter1);
        invitation1.setInvited(invited1);

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setId(2L);
        invitation2.setInviter(inviter2);
        invitation2.setInvited(invited2);

        GoalInvitation invitation3 = new GoalInvitation();
        invitation3.setId(3L);
        invitation3.setInviter(inviter1);
        invitation3.setInvited(invited2);

        List<GoalInvitation> goalInvitations = new ArrayList<>();
        goalInvitations.add(invitation1);
        goalInvitations.add(invitation2);
        goalInvitations.add(invitation3);

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filter);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Test get invitations with filters is nullable")
    public void testGetInvitationsWhenFiltersIsNull() {
        InvitationFilterDto filter = new InvitationFilterDto();

        User inviter = new User();
        inviter.setId(1L);
        inviter.setUsername("Alexander Bulgakov");

        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(1L);
        invitation.setInviter(inviter);

        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setId(1L);
        goalInvitationDto.setInviterId(inviter.getId());

        List<GoalInvitation> goalInvitations = new ArrayList<>();
        goalInvitations.add(invitation);

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);
        when(goalInvitationMapper.toDto(invitation)).thenReturn(goalInvitationDto);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filter);

        assertEquals(1, result.size());
    }
}
