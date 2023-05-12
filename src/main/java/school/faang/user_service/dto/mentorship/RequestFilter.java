package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Size;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RequestFilter {
    @Size(max = 255, message = "no more than 255 characters for descriptionPattern")
    private String descriptionPattern;
    @Size(max = 255, message = "no more than 255 characters for requesterNamePattern")
    private String requesterNamePattern;
    @Size(max = 255, message = "no more than 255 characters for receiverNamePattern")
    private String receiverNamePattern;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
    private int page;
    private int pageSize;
}
