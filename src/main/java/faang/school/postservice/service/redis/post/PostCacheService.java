package faang.school.postservice.service.redis.post;

import faang.school.postservice.dto.kafka.post.KafkaPostDto;
import faang.school.postservice.dto.post.PostDto;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public interface PostCacheService {
    void putPostAndAuthorInCache(PostDto post);

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    void addPostInFeed(KafkaPostDto kafkaPostDto);

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    void deletePostFromFeed(KafkaPostDto kafkaPostDto);

    void deletePostFromCache(PostDto post);

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    void updatePostInCache(PostDto post);
}
