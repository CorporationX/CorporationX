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
public class InviterIdFilterTest {

    private InviterIdFilter inviterIdFilter;
    private List<GoalInvitation> goalInvitations;

    @BeforeEach
    public void setUp() {
        inviterIdFilter = new InviterIdFilter();
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
        invitation1.setInviter(inviter2);

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setId(2L);
        invitation2.setInvited(invited2);
        invitation2.setInviter(inviter);

        goalInvitations.add(invitation1);
        goalInvitations.add(invitation2);
    }

    @Test
    public void testIsApplicable_WithInviterId_ReturnsTrue() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInviterId(2L);

        assertTrue(inviterIdFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable_WithoutInviterId_ReturnsFalse() {
        InvitationFilterDto filterDto = new InvitationFilterDto();

        assertFalse(inviterIdFilter.isApplicable(filterDto));
    }

    @Test
    public void testApply_FilterInviterIdMatches_RemovesMatchingGoalInvitations() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInviterId(3L);

        inviterIdFilter.apply(goalInvitations, filterDto);

        assertEquals(1, goalInvitations.size());
        assertFalse(goalInvitations.stream().anyMatch(g -> g.getId() == 3L));
    }

    @Test
    public void testApply_FilterInviterIdDoesNotMatch_NoChangesToGoalInvitations() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInviterId(5L);

        inviterIdFilter.apply(goalInvitations, filterDto);

        assertEquals(0, goalInvitations.size());
    }
}