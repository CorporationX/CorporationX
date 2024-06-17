package faang.school.postservice.service.hashtag.cache;

import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class HashtagCacheServiceImpl implements HashtagCacheService {

    @Value("${spring.data.redis.cache.hashtag.max_size}")
    private long maxCacheSize;
    private final RedisTemplate<String, Post> redisTemplate;
    private final ZSetOperations<String, Post> zSetOps;

    public HashtagCacheServiceImpl(RedisTemplate<String, Post> redisTemplate) {
        this.redisTemplate = redisTemplate;
        zSetOps = redisTemplate.opsForZSet();
    }

    @Override
    public Set<Post> getPostsByHashtag(String hashtag) {

        if (Boolean.TRUE.equals(redisTemplate.hasKey(hashtag))) {
            return zSetOps.reverseRange(hashtag, 0 , maxCacheSize);
        }

        return null;
    }

    public Set<Post> getPagePostsByHashtag(String hashtag, int offset) {

        if (Boolean.TRUE.equals(redisTemplate.hasKey(hashtag))) {
            return zSetOps.reverseRange(hashtag, offset , 20);
        }

        return null;
    }

    @Override
    public void addPostToHashtag(String hashtag, Post post) {

        double score = post.getLikes().size();
        zSetOps.add(hashtag, post, score);

        log.info("Added post {} to hashtag's #{} cache", post, hashtag);

        Long size = zSetOps.size(hashtag);
        if (size != null && size > maxCacheSize) {
            zSetOps.popMin(hashtag);

            log.info("Removed least popular post from hashtag's #{} cache", hashtag);
        }
    }

    @Override
    public void removePostFromHashtag(String hashtag, Post post) {
        zSetOps.remove(hashtag, post);
        log.info("Removed post {} from hashtag's #{} cache", post, hashtag);
    }
}
