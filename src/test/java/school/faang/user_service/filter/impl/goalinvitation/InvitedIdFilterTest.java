package school.faang.user_service.filter.impl.goalinvitation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexander Bulgakov
 */

@ExtendWith(MockitoExtension.class)
public class InvitedIdFilterTest {

    private InvitedIdFilter invitedIdFilter;
    private List<GoalInvitation> goalInvitations;

    @BeforeEach
    public void setUp() {
        invitedIdFilter = new InvitedIdFilter();
        goalInvitations = new ArrayList<>();

        User invited = new User();
        invited.setId(2L);
        invited.setUsername("Alice");


        User inviter = new User();
        inviter.setId(1L);
        inviter.setUsername("John");

        User invited2 = new User();
        invited2.setId(4L);
        invited2.setUsername("Charlie");

        User inviter2 = new User();
        inviter2.setId(3L);
        inviter2.setUsername("Bob");

        GoalInvitation invitation1 = new GoalInvitation();
        invitation1.setId(1L);
        invitation1.setInvited(invited);
        invitation1.setInviter(inviter);

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setId(2L);
        invitation2.setInvited(invited2);
        invitation2.setInviter(inviter2);

        goalInvitations.add(invitation1);
        goalInvitations.add(invitation2);
    }

    @Test
    public void testIsApplicable_WithInvitedId_ReturnsTrue() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInvitedId(2L);

        assertTrue(invitedIdFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable_WithoutInvitedId_ReturnsFalse() {
        InvitationFilterDto filterDto = new InvitationFilterDto();

        assertFalse(invitedIdFilter.isApplicable(filterDto));
    }

    @Test
    public void testApply_FilterInvitedIdMatches_RemovesMatchingGoalInvitations() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInvitedId(2L);

        invitedIdFilter.apply(goalInvitations, filterDto);

        assertEquals(1, goalInvitations.size());
        assertFalse(goalInvitations.stream().anyMatch(g -> g.getId() == 2L));
    }

    @Test
    public void testApply_FilterInvitedIdDoesNotMatch_NoChangesToGoalInvitations() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInvitedId(5L);

        invitedIdFilter.apply(goalInvitations, filterDto);

        assertEquals(0, goalInvitations.size());
    }
}