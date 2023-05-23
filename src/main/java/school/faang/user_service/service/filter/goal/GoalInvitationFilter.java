package school.faang.user_service.service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public abstract class GoalInvitationFilter {
    public Stream<GoalInvitation> applyFilter(Stream<GoalInvitation> invitations, InvitationFilterDto filter) {
        return invitations.filter(invitation -> applyFilter(invitation, filter));
    }

    protected abstract boolean applyFilter(GoalInvitation invitation, InvitationFilterDto filter);

    public abstract boolean isApplicable(InvitationFilterDto filter);
}