package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.messaging.FollowerEvent;

@Service
@RequiredArgsConstructor
public class FollowerPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followerTopic;
    private final ObjectMapper objectMapper;

    public void publish(FollowerEvent event) {
        try {
            redisTemplate.convertAndSend(followerTopic.getTopic(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
