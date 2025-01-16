package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String URL_KEY_PREFIX = "url:";
    private static final Duration CACHE_TTL = Duration.ofDays(30);

    public void saveUrl(String hash, String originalUrl) {
        String key = URL_KEY_PREFIX + hash;
        redisTemplate.opsForValue().set(key, originalUrl, CACHE_TTL);
    }

    public Optional<String> getUrl(String hash) {
        String key = URL_KEY_PREFIX + hash;
        String url = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(url);
    }
}