package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.LikePostEvent;
import faang.school.postservice.event.NewCommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikePostPublisher implements MessagePublisher<LikePostEvent> {

    @Value("${spring.data.channel.like_post_channel.name}")
    private String channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final NewTopic newCommentTopic;


    @Override
    public void publish(LikePostEvent event) {
        try {
            redisTemplate.convertAndSend(channelTopic, event);
            log.info("Published like post event to Redis - {}:{}", channelTopic, event);

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(newCommentTopic.name(), message);
            log.info("Published new comment event to Kafka - {}: {}", newCommentTopic.name(), message);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing like post event", e);
        }
    }
}
