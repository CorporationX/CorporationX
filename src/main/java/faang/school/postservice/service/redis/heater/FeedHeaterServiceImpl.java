package faang.school.postservice.service.redis.heater;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.event.heat.HeatUsersFeedEvent;
import faang.school.postservice.kafka.producer.HeatUsersFeedProducer;
import faang.school.postservice.service.redis.CachedEntityBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedHeaterServiceImpl implements FeedHeaterService {
    private final UserServiceClient userServiceClient;
    private final HeatUsersFeedProducer producer;
    private final CachedEntityBuilder cachedEntityBuilder;

    @Value("${batch.feed.heat}")
    private int feedHeatSize;

    @Override
    public void feedHeat() {
        Set<Long> userIds = userServiceClient.getAllUsersIds();
        sendUsersBatch(userIds);
    }

    @Async("sendUsersFeedExecutor")
    protected void sendUsersBatch(Set<Long> userIds) {
        try {
            Set<Long> localBatch = new HashSet<>();
            for (Long userId : userIds) {
                localBatch.add(userId);
                if (localBatch.size() >= feedHeatSize) {
                    HeatUsersFeedEvent event = new HeatUsersFeedEvent(new HashSet<>(localBatch));
                    producer.publish(event);
                    localBatch.clear();
                }
            }
            if (!localBatch.isEmpty()) {
                HeatUsersFeedEvent event = new HeatUsersFeedEvent(new HashSet<>(localBatch));
                producer.publish(event);
            }
        } catch (Exception e) {
            log.error("Error in sendUsersBatch: ", e);
        }
    }

    @Override
    @Async("generateUsersFeedExecutor")
    public void generateUsersFeed(HeatUsersFeedEvent event) {
        try {
            event.getUsersIds().forEach(cachedEntityBuilder::buildAndSaveNewRedisFeed);
        } catch (Exception e) {
            log.error("Error in generateUsersFeed: ", e);
        }
    }
}