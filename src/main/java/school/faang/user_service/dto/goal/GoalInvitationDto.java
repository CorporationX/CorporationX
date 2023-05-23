package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class GoalInvitationDto {
    private Long id;
    private Long inviterId;
    private Long invitedUserId;
    private Long goalId;
    private RequestStatus status;
}