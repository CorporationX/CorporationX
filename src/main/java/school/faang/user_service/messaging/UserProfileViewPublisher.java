package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.messaging.FollowerEvent;
import school.faang.user_service.dto.messaging.UserProfileViewEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserProfileViewPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic profileViewTopic;
    private final ObjectMapper objectMapper;

    @Async
    public void publish(long userId, long viewerId) {
        UserProfileViewEvent event = UserProfileViewEvent.builder()
                .userId(userId)
                .viewerId(viewerId)
                .viewedAt(LocalDateTime.now())
                .build();
        try {
            redisTemplate.convertAndSend(profileViewTopic.getTopic(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
