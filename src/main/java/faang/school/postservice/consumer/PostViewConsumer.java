package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.LikePostEvent;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.exception.ListenerException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.service.redis.post.PostCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostViewConsumer {

    private final PostCacheService postCacheService;
    private final ObjectMapper objectMapper;
    private final PostMapper postMapper;

    public PostViewConsumer(PostCacheService postCacheService, ObjectMapper objectMapper, PostMapper postMapper) {
        this.postCacheService = postCacheService;
        this.objectMapper = objectMapper;
        this.postMapper = postMapper;
    }

    @KafkaListener(topics = "${spring.data.channel.post_view.name}", groupId = "${spring.data.kafka.group-id}")
    public void listen(String event) {
        try {
            PostViewEvent postViewEvent = objectMapper.readValue(event, PostViewEvent.class);
            log.info("Received new postViewEvent {}", event);

            PostDto dto = postMapper.toDto(postViewEvent);
            postCacheService.addPostView(dto);
        } catch (JsonProcessingException e) {
            log.error("Error processing event JSON: {}", event, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing event: {}", event, e);
            throw new ListenerException(e.getMessage());
        }
    }
}