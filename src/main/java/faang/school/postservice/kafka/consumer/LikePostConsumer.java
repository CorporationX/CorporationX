package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.entity.dto.like.LikeDto;
import faang.school.postservice.event.LikePostEvent;
import faang.school.postservice.exception.ListenerException;
import faang.school.postservice.mapper.LikeMapper;
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
    private final LikeMapper likeMapper;

    public LikePostConsumer(LikeCacheService likeCacheService, ObjectMapper objectMapper, LikeMapper likeMapper) {
        this.likeCacheService = likeCacheService;
        this.objectMapper = objectMapper;
        this.likeMapper = likeMapper;
    }

    @KafkaListener(topics = "${spring.data.channel.like_post_channel.name}", groupId = "${spring.data.kafka.group-id}")
    public void listen(String event) {
        try {
            LikePostEvent likePostEvent = objectMapper.readValue(event, LikePostEvent.class);
            log.info("Received new likePostEvent {}", event);

            LikeDto dto = likeMapper.toDto(likePostEvent);
            likeCacheService.addLikeOnPost(dto);
        } catch (JsonProcessingException e) {
            log.error("Error processing event JSON: {}", event, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing event: {}", event, e);
            throw new ListenerException(e.getMessage());
        }
    }
}