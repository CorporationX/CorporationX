package school.faang.user_service.mapper.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class GoalInvitationMapperTest {

    @Spy
    private GoalInvitationMapper goalInvitationMapper = new GoalInvitationMapperImpl();


    @Test
    @DisplayName("Test to dto")
    public void testToDto() {
        GoalInvitation invitation = mock(GoalInvitation.class);
        Goal goal = mock(Goal.class);
        User inviter = mock(User.class);
        User invited = mock(User.class);

        when(invitation.getInviter()).thenReturn(inviter);
        when(invitation.getInvited()).thenReturn(invited);
        when(inviter.getId()).thenReturn(1L);
        when(invited.getId()).thenReturn(2L);

        GoalInvitationDto dto = goalInvitationMapper.toDto(invitation);

        assertEquals(1L, dto.getInviterId());
        assertEquals(2L, dto.getInvitedUserId());
    }

    @Test
    @DisplayName("Test to entity")
    public void testToEntity() {
        GoalInvitationDto dto = mock(GoalInvitationDto.class);
        dto.setInviterId(1L);
        dto.setInvitedUserId(2L);

        User inviter = new User();
        inviter.setId(1L);

        User invited = new User();
        invited.setId(2L);

        when(dto.getId()).thenReturn(1L);

        GoalInvitation invitation = goalInvitationMapper.toEntity(dto);
        invitation.setInviter(inviter);
        invitation.setInvited(invited);

        assertEquals(1L, invitation.getInviter().getId());
        assertEquals(2L, invitation.getInvited().getId());
    }
}
