package faang.school.postservice.publisher;

import faang.school.postservice.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class LikeEventPublisher implements MessagePublisher<LikeEvent> {

    @Value("${spring.data.redis.channels.like_post_channel.name}")
    private String channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(LikeEvent event) {
        redisTemplate.convertAndSend(channelTopic, event);
        log.info("Published goal completed event - {}:{}", channelTopic, event);
    }
}
