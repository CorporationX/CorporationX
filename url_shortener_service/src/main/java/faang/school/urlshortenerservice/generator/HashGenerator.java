package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final HashGeneratorService hashGeneratorService;

    @Value("${url.shortener.hash.batch-size}")
    private int batchSize;

    @Async
    public CompletableFuture<Void> generateHashes() {
        try {
            log.info("Starting hash generation with batch size: {}", batchSize);
            List<Long> sequences = hashRepository.getNextSequenceValues(batchSize);
            log.info("Retrieved {} sequence values from database", sequences.size());

            hashGeneratorService.generateHashesInternal(sequences);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error generating hashes", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Transactional
    public List<String> getAvailableHashes(int limit) {
        log.info("Requesting {} available hashes", limit);
        var hashes = hashRepository.findUnusedHashes(limit);
        log.info("Retrieved {} hashes from database", hashes.size());
        return hashes;
    }

    @Transactional(readOnly = true)
    public long getUnusedHashesCount() {
        long count = hashRepository.countUnusedHashes();
        log.info("Current unused hashes count: {}", count);
        return count;
    }
}