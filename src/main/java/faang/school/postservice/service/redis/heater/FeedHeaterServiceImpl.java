package faang.school.postservice.service.redis.heater;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisFeed;
import faang.school.postservice.event.heat.HeatUsersFeedEvent;
import faang.school.postservice.kafka.producer.HeatUsersFeedProducer;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedHeaterServiceImpl implements FeedHeaterService {
    private final UserServiceClient userServiceClient;
    private final HeatUsersFeedProducer producer;
    private final RedisFeedRepository redisFeedRepository;
    private final PostService postService;

    @Value("${spring.data.redis.ttl.feed}")
    private Long feedTtl;

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
            event.getUsersIds().forEach(this::buildAndSaveNewRedisFeed);
        } catch (Exception e) {
            log.error("Error in generateUsersFeed: ", e);
        }
    }

    @Retryable(value = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 1000))
    public RedisFeed buildAndSaveNewRedisFeed(Long userId) {
        LinkedHashSet<Long> postsIds = postService.findUserFollowingsPosts(userId, LocalDateTime.now(), 500).stream()
                .map(PostDto::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        RedisFeed redisFeed = RedisFeed.builder()
                .userId(userId)
                .postsIds(postsIds)
                .version(1L)
                .ttl(feedTtl)
                .build();

        redisFeedRepository.save(redisFeed.getUserId(), redisFeed);
        return redisFeed;
    }
}