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
public class InviterNamePattern implements Filter<InvitationFilterDto, GoalInvitation> {
    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getInviterNamePattern() != null;
    }

    @Override
    public List<GoalInvitation> apply(List<GoalInvitation> goalInvitations, InvitationFilterDto filter) {
        return goalInvitations.stream().filter(goalInvitation ->
                goalInvitation.getInviter()
                        .getUsername()
                        .matches(filter.getInviterNamePattern()))
                .collect(Collectors.toList());
    }
}
