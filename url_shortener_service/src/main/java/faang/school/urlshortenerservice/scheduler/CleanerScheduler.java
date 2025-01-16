package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final CacheManager cacheManager;


    @Value("${url.cleaner.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${url.cleaner.cron}")
    public void cleanExpiredUrls() {
        log.info("Starting cleanup of expired URLs");

        try {
            processExpiredUrls();
        } catch (Exception e) {
            log.error("Error during URL cleanup", e);
        }
    }

    @Transactional
    protected void processExpiredUrls() {
        List<String> freedHashes = urlRepository.deleteExpiredAndReturnHashes(LocalDateTime.now());

        if (freedHashes.isEmpty()) {
            log.info("No expired URLs found for cleanup");
            return;
        }

        Cache urlCache = cacheManager.getCache("urls");
        if (urlCache != null) {
            freedHashes.forEach(urlCache::evict);
            log.info("Cleared cache for {} expired URLs", freedHashes.size());
        }

        int totalProcessed = 0;
        int failedToSave = 0;

        for (List<String> batch : partition(freedHashes, batchSize)) {
            totalProcessed += processBatch(batch);
            failedToSave += batch.size() - totalProcessed;
        }

        log.info("Cleanup completed. Processed {} hashes, {} failed to save, {} total",
                totalProcessed, failedToSave, freedHashes.size());
    }

    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    protected int processBatch(List<String> batch) {
        try {
            hashRepository.saveHashBatch(batch);
            return batch.size();
        } catch (Exception e) {
            log.warn("Failed to save batch, falling back to individual inserts");
            return processIndividualHashes(batch);
        }
    }

    private int processIndividualHashes(List<String> hashes) {
        int successCount = 0;
        for (String hash : hashes) {
            try {
                hashRepository.saveHash(hash);
                successCount++;
            } catch (DataIntegrityViolationException e) {
                log.debug("Hash already exists: {}", hash);
            } catch (Exception e) {
                log.warn("Failed to save hash: {}", hash, e);
            }
        }
        return successCount;
    }

    private List<List<String>> partition(List<String> list, int size) {
        List<List<String>> parts = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(list.size(), i + size)))
            );
        }
        return parts;
    }
}