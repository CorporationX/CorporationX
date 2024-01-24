package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.impl.goalinvitation.InvitedIdFilter;
import school.faang.user_service.filter.impl.goalinvitation.InvitedNamePattern;
import school.faang.user_service.filter.impl.goalinvitation.InviterIdFilter;
import school.faang.user_service.filter.impl.goalinvitation.InviterNamePattern;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    private GoalInvitationRepository goalInvitationRepository;
    private GoalInvitationMapper goalInvitationMapper;
    private GoalInvitationValidator goalInvitationValidator;
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
        goalInvitationValidator = new GoalInvitationValidator();

        filters = List.of(inviterNamePattern, invitedNamePattern, invitedIdFilter, inviterIdFilter);

        goalInvitationService = new GoalInvitationService(goalInvitationRepository,
                goalInvitationMapper, goalInvitationValidator, filters);
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
