package faang.school.postservice.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewProducer implements MessageProducer<PostViewEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final NewTopic postViewTopic;


    @Override
    public void publish(PostViewEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            if(!event.getAuthorId().equals(event.getViewerId())){
                kafkaTemplate.send(postViewTopic.name(), message);
                log.info("Published new post view event to Kafka - {}: {}", postViewTopic.name(), message);
            }
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing post view event", e);
        }
    }
}