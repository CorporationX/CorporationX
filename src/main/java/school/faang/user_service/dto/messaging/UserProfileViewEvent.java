package school.faang.user_service.dto.messaging;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileViewEvent {
    private long userId;
    private long viewerId;
    private LocalDateTime viewedAt;
}
