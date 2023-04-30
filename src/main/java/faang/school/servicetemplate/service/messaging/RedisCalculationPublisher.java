package faang.school.servicetemplate.service.messaging;

import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.servicetemplate.model.CalculationRequest;
import faang.school.servicetemplate.model.CalculationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RedisCalculationPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    private final ChannelTopic topic;

    @Autowired
    public RedisCalculationPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Scheduled(fixedDelay = 5000L)
    public void produce() {
        Random random = new Random();
        int randomCalculationTypeNumber = random.nextInt(0, CalculationType.values().length);
        var randomCalculationRequest = new CalculationRequest(
                random.nextInt(),
                random.nextInt(),
                CalculationType.values()[randomCalculationTypeNumber]
        );
        publish(randomCalculationRequest);
    }

    private void publish(CalculationRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            redisTemplate.convertAndSend(topic.getTopic(), objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
