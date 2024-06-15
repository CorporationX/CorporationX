package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipStartEvent;
import school.faang.user_service.mapper.JsonMapper;

@Component
public class MentorshipEventPublisher extends AbstractPublisher<MentorshipStartEvent> {
    public MentorshipEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                    @Value("${spring.data.redis.channels.mentorship-channel.name}") String channelName,
                                    JsonMapper<MentorshipStartEvent> jsonMapper) {
        super(redisTemplate, channelName, jsonMapper);
    }
}