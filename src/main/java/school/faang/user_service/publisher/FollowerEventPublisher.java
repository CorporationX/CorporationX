package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEvent;
import school.faang.user_service.mapper.JsonMapper;

@Component
public class FollowerEventPublisher extends AbstractPublisher<FollowerEvent> {
    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  @Value("${spring.data.redis.channels.follower-event-channel.name}") String channelName,
                                  JsonMapper<FollowerEvent> jsonMapper) {
        super(redisTemplate, channelName, jsonMapper);
    }
}