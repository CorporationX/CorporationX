package faang.school.postservice.service.hashtag.cache;

import faang.school.postservice.dto.post.PostHashtagDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class HashtagCacheServiceImpl implements HashtagCacheService {

    @Value("${spring.data.redis.cache.hashtag.max_size}")
    private long maxCacheSize;
    private final RedisTemplate<String, PostHashtagDto> redisTemplate;
    private final ZSetOperations<String, PostHashtagDto> zSetOps;

    public HashtagCacheServiceImpl(RedisTemplate<String, PostHashtagDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
        zSetOps = redisTemplate.opsForZSet();
    }

    @Override
    public Set<PostHashtagDto> getPostsByHashtag(String hashtag, Pageable pageable) {

        int end = pageable.getPageNumber() == 0 ?
                pageable.getPageSize() :
                pageable.getPageNumber() * pageable.getPageSize();

        if (end <= maxCacheSize && Boolean.TRUE.equals(redisTemplate.hasKey(hashtag))) {
            return zSetOps.reverseRange(hashtag, pageable.getOffset(), pageable.getPageSize());
        }

        return null;
    }

    @Override
    public void addPostToHashtag(String hashtag, PostHashtagDto post) {

        double score = post.getLikeIds().size();
        zSetOps.add(hashtag, post, score);

        log.info("Added post {} to hashtag's #{} cache", post, hashtag);

        Long size = zSetOps.size(hashtag);
        if (size != null && size > maxCacheSize) {
            zSetOps.popMin(hashtag);

            log.info("Removed least popular post from hashtag's #{} cache", hashtag);
        }
    }

    @Override
    public void removePostFromHashtag(String hashtag, PostHashtagDto post) {
        zSetOps.remove(hashtag, post);
        log.info("Removed post {} from hashtag's #{} cache", post, hashtag);
    }
}
