package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class InvitationFilterDto {
    private String inviterNamePattern;
    private String invitedNamePattern;
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
    private int page;
    private int pageSize;
}