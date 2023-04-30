package faang.school.servicetemplate.service.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.servicetemplate.model.CalculationRequest;
import faang.school.servicetemplate.service.worker.CalculationWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisCalculationSubscriber implements MessageListener {

    private final CalculationWorker calculationWorker;

    @Autowired
    public RedisCalculationSubscriber(CalculationWorker calculationWorker) {
        this.calculationWorker = calculationWorker;
    }

    public List<CalculationRequest> receivedRequests = new ArrayList<>();

    public void onMessage(final Message message, final byte[] pattern) {
        log.info("Message received: " + new String(message.getBody()));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            var calculationRequest = objectMapper.readValue(message.getBody(), CalculationRequest.class);
            receivedRequests.add(calculationRequest);
            calculationWorker.processCalculationAsync(calculationRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
