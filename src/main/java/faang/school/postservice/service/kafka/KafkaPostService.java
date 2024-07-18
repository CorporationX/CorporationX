package faang.school.postservice.service.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.event.post.NewPostEvent;
import faang.school.postservice.kafka.producer.NewPostProducer;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class KafkaPostService {

    private final UserServiceClient userServiceClient;
    private final NewPostProducer newPostProducer;

    @Value("${batch.get-user-followers}")
    private int batchSize;

    @Retryable(value = FeignException.class, maxAttempts = 5)
    public void sendPostToPublisher(PostDto dto) {
        List<Long> followers = userServiceClient.getFollowerIdsByAuthorId(dto.getAuthorId());
        SortedSet<Long> sortedFollowers = new TreeSet<>(followers);

        ListUtils.partition(new ArrayList<>(sortedFollowers), batchSize).forEach(followersPartition -> {
            SortedSet<Long> sortedPartition = new TreeSet<>(followersPartition);
            NewPostEvent event = NewPostEvent.builder()
                    .postDto(dto)
                    .followersIds(sortedPartition)
                    .build();
            newPostProducer.publish(event);
        });
    }
}