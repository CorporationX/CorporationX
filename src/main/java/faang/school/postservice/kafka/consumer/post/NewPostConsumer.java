package faang.school.postservice.kafka.consumer.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.NewPostEvent;
import faang.school.postservice.exception.ListenerException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.service.redis.feed.FeedCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewPostConsumer {

    private final FeedCacheService feedCacheService;
    private final ObjectMapper objectMapper;

    public NewPostConsumer(FeedCacheService feedCacheService, ObjectMapper objectMapper) {
        this.feedCacheService = feedCacheService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.data.channel.new_post.name}", groupId = "${spring.data.kafka.group-id}")
    public void listen(String event) {
        try {
            NewPostEvent postViewEvent = objectMapper.readValue(event, NewPostEvent.class);
            log.info("Received new NewPostEvent {}", event);
            feedCacheService.addPostInFeed(postViewEvent);
        } catch (JsonProcessingException e) {
            log.error("Error processing event JSON: {}", event, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing event: {}", event, e);
            throw new ListenerException(e.getMessage());
        }
    }
}