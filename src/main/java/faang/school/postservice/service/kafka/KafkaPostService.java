package faang.school.postservice.service.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.event.post.NewPostEvent;
import faang.school.postservice.kafka.producer.NewPostProducer;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPostService {

    private final UserServiceClient userServiceClient;
    private final NewPostProducer newPostProducer;

    @Value("${batch.get-user-followers}")
    private int batchSize;

    @Retryable(value = FeignException.class, maxAttempts = 5)
    public void sendPostToPublisher(PostDto dto) {
        Set<Long> followerIds = fetchFollowerIds(dto.getAuthorId());
        List<List<Long>> followerBatches = partitionFollowerIds(followerIds);

        followerBatches.forEach(followerBatch -> publishNewPostEvent(dto, followerBatch));
    }

    private Set<Long> fetchFollowerIds(Long authorId) {
        log.info("Fetching followers for author with ID {}", authorId);
        return userServiceClient.getFollowers(authorId).stream()
                .map(UserDto::getId)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private List<List<Long>> partitionFollowerIds(Set<Long> followerIds) {
        log.info("Partitioning {} followers into batches of {}", followerIds.size(), batchSize);
        return ListUtils.partition(new ArrayList<>(followerIds), batchSize);
    }

    private void publishNewPostEvent(PostDto dto, List<Long> followerBatch) {
        HashSet<Long> sortedFollowerBatch = new HashSet<>(followerBatch);
        NewPostEvent event = NewPostEvent.builder()
                .postDto(dto)
                .followersIds(sortedFollowerBatch)
                .build();
        log.info("Publishing new post event for post ID {} to {} followers", dto.getId(), sortedFollowerBatch.size());
        newPostProducer.publish(event);
    }
}