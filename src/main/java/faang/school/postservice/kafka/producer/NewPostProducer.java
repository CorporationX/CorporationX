package faang.school.postservice.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.NewPostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewPostProducer implements MessageProducer<NewPostEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final NewTopic newPostTopic;


    @Override
    public void publish(NewPostEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(newPostTopic.name(), message);
            log.info("Published new post event to Kafka - {}: {}", newPostTopic.name(), message);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing new post event", e);
        }
    }
}