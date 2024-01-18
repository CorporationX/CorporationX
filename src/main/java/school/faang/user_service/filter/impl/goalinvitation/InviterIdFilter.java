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
public class InviterIdFilter implements Filter<InvitationFilterDto, GoalInvitation> {
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return filterDto.getInviterId() != null;
    }

    @Override
    public List<GoalInvitation> apply(List<GoalInvitation> goalInvitations, InvitationFilterDto filterDto) {
        return goalInvitations.stream()
                .filter(goalInvitation ->
                        filterDto.getInviterId().equals(goalInvitation.getInviter().getId()))
                .collect(Collectors.toList());
    }
}
