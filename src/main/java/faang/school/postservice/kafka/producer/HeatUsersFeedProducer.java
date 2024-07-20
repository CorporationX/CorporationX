package faang.school.postservice.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.comment.NewCommentEvent;
import faang.school.postservice.event.heat.HeatUsersFeedEvent;
import faang.school.postservice.event.like.LikePostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeatUsersFeedProducer implements MessageProducer<HeatUsersFeedEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final NewTopic heatUsersFeedTopic;


    @Override
    public void publish(HeatUsersFeedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(heatUsersFeedTopic.name(), message);
            log.info("Published new heat users feed event to Kafka - {}: {}", heatUsersFeedTopic.name(), message);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing new comment event", e);
        }
    }
}
