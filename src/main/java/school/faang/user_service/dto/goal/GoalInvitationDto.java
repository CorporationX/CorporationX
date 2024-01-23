package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

/**
 * @author Alexander Bulgakov
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoalInvitationDto {

    private Long id;

    private Long inviterId;

    private Long invitedUserId;

    private Long goalId;

    private RequestStatus status;
}
