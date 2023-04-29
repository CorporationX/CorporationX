package school.faang.user_service.dto.mentorship;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RequestFilter {
    private String descriptionPattern;
    private String requesterNamePattern;
    private String receiverNamePattern;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
    private int page;
    private int pageSize;
}
