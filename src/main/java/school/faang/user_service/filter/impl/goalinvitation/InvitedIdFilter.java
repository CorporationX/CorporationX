package school.faang.user_service.filter.impl.goalinvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.Filter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexander Bulgakov
 */
@Component
public class InvitedIdFilter implements Filter<InvitationFilterDto, GoalInvitation> {
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return filterDto.getInvitedId() != null;
    }

    @Override
    public List<GoalInvitation> apply(List<GoalInvitation> goalInvitations, InvitationFilterDto filterDto) {
        return goalInvitations.stream()
                .filter(goalInvitation ->
                        filterDto.getInvitedId().equals(goalInvitation.getInvited().getId()))
                .collect(Collectors.toList());
    }
}
