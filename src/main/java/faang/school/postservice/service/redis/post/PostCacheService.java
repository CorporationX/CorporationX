package faang.school.postservice.service.redis.post;

import faang.school.postservice.dto.kafka.post.KafkaPostDto;
import faang.school.postservice.dto.post.PostDto;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public interface PostCacheService {
    void putPostAndAuthorInCache(PostDto post);

    void addPostInFeed(KafkaPostDto kafkaPostDto);

    void deletePostFromFeed(KafkaPostDto kafkaPostDto);

    void deletePostFromCache(PostDto post);

    void updatePostInCache(PostDto post);

    void addPostView(PostDto postDto);
}
