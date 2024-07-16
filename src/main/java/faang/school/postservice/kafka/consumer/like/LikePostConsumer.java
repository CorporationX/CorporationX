package faang.school.postservice.kafka.consumer.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.like.LikePostEvent;
import faang.school.postservice.exception.ListenerException;
import faang.school.postservice.service.redis.like.LikeCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LikePostConsumer {

    private final LikeCacheService likeCacheService;
    private final ObjectMapper objectMapper;

    public LikePostConsumer(LikeCacheService likeCacheService, ObjectMapper objectMapper) {
        this.likeCacheService = likeCacheService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.data.channel.like_post_channel.name}", groupId = "${spring.data.kafka.group-id}")
    public void listen(String event) {
        try {
            LikePostEvent likePostEvent = objectMapper.readValue(event, LikePostEvent.class);
            log.info("Received new likePostEvent {}", event);
            likeCacheService.addLikeOnPost(likePostEvent);
        } catch (JsonProcessingException e) {
            log.error("Error processing event JSON: {}", event, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing event: {}", event, e);
            throw new ListenerException(e.getMessage());
        }
    }
}