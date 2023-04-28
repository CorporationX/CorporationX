package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.entity.recommendation.RequestStatus;

import java.util.Set;

@Data
public class RequestFilterDto {
    private Long requesterId;
    private Long receiverId;
    private Set<Long> skills;
    private RequestStatus status;
    private int page;
    private int pageSize;
}
