package faang.school.urlshortenerservice.config.cash;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class CashConfig {

    @Value("${url.shortener.cache.capacity}")
    private int cacheCapacity;

    @Bean
    public BlockingQueue<String> hashBlockingQueue() {
        return new ArrayBlockingQueue<>(cacheCapacity);
    }

    @Bean
    public AtomicBoolean refillFlag() {
        return new AtomicBoolean(false);
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("urls");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(10)));
        return cacheManager;
    }
}