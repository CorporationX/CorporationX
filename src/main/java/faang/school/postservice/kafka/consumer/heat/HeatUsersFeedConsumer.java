package faang.school.postservice.kafka.consumer.heat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.heat.HeatUsersFeedEvent;
import faang.school.postservice.exception.ListenerException;
import faang.school.postservice.service.redis.heater.FeedHeaterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeatUsersFeedConsumer {

    private final FeedHeaterService feedHeaterService;
    private final ObjectMapper objectMapper;

    public HeatUsersFeedConsumer(FeedHeaterService feedHeaterService, ObjectMapper objectMapper) {
        this.feedHeaterService = feedHeaterService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.data.channel.heat_users_feed.name}", groupId = "${spring.data.kafka.group-id}")
    public void listen(String event) {

        try {
            HeatUsersFeedEvent heatUsersFeedEvent = objectMapper.readValue(event, HeatUsersFeedEvent.class);
            log.info("Received new heatUsersFeedEvent {}", event);
            feedHeaterService.generateUsersFeed(heatUsersFeedEvent);
        } catch (JsonProcessingException e) {
            log.error("Error processing event JSON: {}", event, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing event: {}", event, e);
            throw new ListenerException(e.getMessage());
        }
    }
}