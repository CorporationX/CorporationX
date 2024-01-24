package school.faang.user_service.validator.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;

/**
 * @author Alexander Bulgakov
 */
@Component
public class GoalInvitationValidator {
    public boolean checkFilter(InvitationFilterDto filter) {
        return filter != null;
    }
}
