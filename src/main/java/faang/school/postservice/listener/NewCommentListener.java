package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.event.NewPaymentEvent;
import faang.school.accountservice.exception.ListenerException;
import faang.school.accountservice.service.payment.PaymentService;
import faang.school.postservice.event.NewCommentEvent;
import faang.school.postservice.exception.ListenerException;
import faang.school.postservice.service.comment.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewCommentListener {

    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    public NewCommentListener(CommentService commentService, ObjectMapper objectMapper) {
        this.commentService = commentService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.data.channel.new_comment.name}", groupId = "${spring.data.kafka.group-id}")
    public void listen(String event) {

        try {
            NewCommentEvent newCommentEvent = objectMapper.readValue(event, NewCommentEvent.class);
            log.info("Received new newCommentEvent {}", event);
            commentService.authorizePayment(newCommentEvent.getUserId(), newCommentEvent.getPaymentId());
        } catch (JsonProcessingException e) {
            log.error("Error processing event JSON: {}", event, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing event: {}", event, e);
            throw new ListenerException(e.getMessage());
        }
    }
}