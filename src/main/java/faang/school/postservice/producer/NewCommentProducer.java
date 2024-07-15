package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.NewCommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewCommentProducer implements MessageProducer<NewCommentEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final NewTopic newCommentTopic;


    @Override
    public void publish(NewCommentEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(newCommentTopic.name(), message);
            log.info("Published new comment event to Kafka - {}: {}", newCommentTopic.name(), message);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing new comment event", e);
        }
    }
}