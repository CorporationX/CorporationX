package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheRefillService {
    private final HashGenerator hashGenerator;

    @Transactional
    public List<String> refillCache(int spaceAvailable) {
        if (spaceAvailable <= 0) {
            return Collections.emptyList();
        }
        log.debug("Refilling cache. Space available: {}", spaceAvailable);
        return hashGenerator.getAvailableHashes(spaceAvailable);
    }

    @Transactional
    public CompletableFuture<Void> generateNewHashes() {
        return hashGenerator.generateHashes();
    }
}