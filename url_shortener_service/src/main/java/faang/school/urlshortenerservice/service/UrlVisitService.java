package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlVisitService {
    private final UrlRepository urlRepository;

    @Transactional
    @CacheEvict(cacheNames = "urls", key = "#hashValue")
    public void incrementVisits(String hashValue) {
        Optional<Long> newCount = urlRepository.incrementVisitsCount(hashValue);
        if (newCount.isEmpty()) {
            log.warn("Failed to increment visits count for hash: {}", hashValue);
        }
    }
}