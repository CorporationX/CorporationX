package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.service.CacheRefillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalHashCache {
    private final HashGenerator hashGenerator;
    private final CacheRefillService cacheRefillService;
    private final BlockingQueue<String> hashCache;
    private final AtomicBoolean isRefilling;

    @Value("${url.shortener.cache.capacity}")
    private int cacheCapacity;

    @Value("${url.shortener.cache.refill-threshold}")
    private int refillThresholdPercent;

    @PostConstruct
    public void init() {
        log.info("Initializing LocalHashCache with capacity: {}", cacheCapacity);
        long unusedHashesCount = hashGenerator.getUnusedHashesCount();
        log.info("Found {} unused hashes in database", unusedHashesCount);

        if (unusedHashesCount < cacheCapacity) {
            log.info("Generating initial batch of hashes...");
            try {
                cacheRefillService.generateNewHashes().get();
                log.info("Initial hash generation completed");
            } catch (Exception e) {
                log.error("Error generating initial hashes", e);
            }
        }

        refillCache();
        log.info("Cache initialized with {} hashes", hashCache.size());
    }

    @Transactional
    public String getNextHash() {
        String hashValue = hashCache.poll();
        log.info("Retrieved hash from cache. Cache size: {}", hashCache.size());

        if (hashValue != null && shouldRefill()) {
            log.info("Cache size below threshold ({}%). Triggering refill...", refillThresholdPercent);
            triggerAsyncRefill();
        }
        return hashValue;
    }

    private boolean shouldRefill() {
        int currentSize = hashCache.size();
        int onePercent = cacheCapacity / 100;
        if (onePercent == 0) return true;

        int currentPercent = currentSize / onePercent;
        log.info("Current cache fill percentage: {}%", currentPercent);
        return currentPercent < refillThresholdPercent;
    }

    private void triggerAsyncRefill() {
        if (isRefilling.compareAndSet(false, true)) {
            log.info("Starting async cache refill");
            cacheRefillService.generateNewHashes()
                    .thenAccept(v -> {
                        int spaceAvailable = cacheCapacity - hashCache.size();
                        List<String> newHashes = cacheRefillService.refillCache(spaceAvailable);
                        addHashesToCache(newHashes);
                        isRefilling.set(false);
                        log.info("Async cache refill completed. New cache size: {}", hashCache.size());
                    })
                    .exceptionally(e -> {
                        log.error("Error during async cache refill", e);
                        isRefilling.set(false);
                        return null;
                    });
        } else {
            log.info("Refill already in progress, skipping");
        }
    }

    private void refillCache() {
        int spaceAvailable = cacheCapacity - hashCache.size();
        List<String> newHashes = cacheRefillService.refillCache(spaceAvailable);
        addHashesToCache(newHashes);
    }

    private void addHashesToCache(List<String> hashes) {
        hashes.forEach(hash -> {
            if (!hashCache.offer(hash)) {
                log.warn("Failed to add hash {} to cache - queue is full", hash);
            }
        });
        log.info("Added {} hashes to cache. New size: {}", hashes.size(), hashCache.size());
    }
}