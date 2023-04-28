package school.faang.user_service.dto.recommendation;

import lombok.Data;

import java.util.Set;

@Data
public class RequestFilterDto {
    private Long requesterId;
    private Long receiverId;
    private Set<Long> skills;
    private int page;
    private int pageSize;
}
