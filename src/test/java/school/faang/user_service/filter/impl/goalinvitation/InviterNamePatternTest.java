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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class InviterNamePatternTest {

    private InviterNamePattern inviterNamePattern;
    private List<GoalInvitation> goalInvitations;

    @BeforeEach
    public void setUp() {
        inviterNamePattern = new InviterNamePattern();
        goalInvitations = new ArrayList<>();
    }

    @Test
    public void testIsApplicable_WithInviterNamePattern_ReturnsTrue() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInviterNamePattern("John");

        boolean result = inviterNamePattern.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicable_WithoutInviterNamePattern_ReturnsFalse() {
        InvitationFilterDto filterDto = new InvitationFilterDto();

        boolean result = inviterNamePattern.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testApply_WithMatchingInviterNamePattern() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInviterNamePattern("Jane");

        User inviter1 = new User();
        inviter1.setUsername("John Doe");

        User inviter2 = new User();
        inviter2.setUsername("Jane Smith");

        GoalInvitation invitation1 = new GoalInvitation();
        invitation1.setId(1L);
        invitation1.setInviter(inviter1);

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setId(2L);
        invitation2.setInviter(inviter2);

        goalInvitations.add(invitation1);
        goalInvitations.add(invitation2);

        inviterNamePattern.apply(goalInvitations, filterDto);

        assertTrue(goalInvitations.size() == 1);
        assertTrue(goalInvitations.get(0).getInviter().getUsername().equals("Jane Smith"));
    }
}

