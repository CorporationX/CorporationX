package school.faang.user_service.dto.messaging;

import lombok.Data;

@Data
public class FollowerEvent {
    private long followerId;
    private long followeeId;
}
