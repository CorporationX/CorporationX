package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entiity.Hash;
import faang.school.urlshortenerservice.entiity.Url;
import faang.school.urlshortenerservice.exception.InvalidHashException;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.LocalHashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validation.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final LocalHashCache hashCache;
    private final UrlValidator urlValidator;
    private final UrlVisitService urlVisitService;

    @Value("${server.host:localhost}:${server.port:8080}")
    private String baseUrl;

    @Transactional
    public String createShortUrl(String originalUrl) {
        log.info("Starting short URL creation process for URL: {}", originalUrl);
        validateUrl(originalUrl);

        String hashValue = generateAndReserveHash();
        saveUrlMapping(hashValue, originalUrl);
        String shortUrl = constructShortUrl(hashValue);

        log.info("Successfully created short URL: {} -> {}", shortUrl, originalUrl);
        return shortUrl;
    }

    private String generateAndReserveHash() {
        String hashValue = hashCache.getNextHash();
        log.debug("Generated new hash: {}", hashValue);

        Hash hash = hashRepository.findByValueAndUsedFalse(hashValue)
                .orElseThrow(() -> {
                    log.error("Failed to find available hash: {}", hashValue);
                    return new RuntimeException("Hash not found or already used: " + hashValue);
                });

        hash.setUsed(true);
        hashRepository.save(hash);
        log.debug("Reserved hash {} in database", hashValue);

        return hashValue;
    }

    private void saveUrlMapping(String hashValue, String originalUrl) {
        Url url = new Url(new Hash(hashValue), originalUrl);
        urlRepository.save(url);
        log.debug("Saved URL mapping to database: {} -> {}", hashValue, originalUrl);

        urlCacheRepository.saveUrl(hashValue, originalUrl);
        log.debug("Cached URL mapping: {} -> {}", hashValue, originalUrl);
    }

    private String constructShortUrl(String hashValue) {
        return baseUrl + "/" + hashValue;
    }

    public ResponseEntity<Void> redirect(String hashValue) {
        log.info("Processing redirect request for hash: {}", hashValue);
        validateHash(hashValue);

        try {
            String originalUrl = getOriginalUrl(hashValue);
            log.info("Redirecting {} to {}", hashValue, originalUrl);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        } catch (UrlNotFoundException e) {
            log.warn("URL not found for hash: {}", hashValue);
            return ResponseEntity.notFound().build();
        }
    }

    public String getOriginalUrl(String hashValue) {
        log.debug("Retrieving original URL for hash: {}", hashValue);

        Optional<String> cachedUrl = urlCacheRepository.getUrl(hashValue);
        if (cachedUrl.isPresent()) {
            String url = cachedUrl.get();
            log.debug("Cache hit: {} -> {}", hashValue, url);
            incrementVisitsAsync(hashValue);
            return url;
        }

        log.debug("Cache miss for hash: {}, checking database", hashValue);

        Url url = urlRepository.findByHashValue(hashValue)
                .orElseThrow(() -> {
                    log.error("URL not found in database for hash: {}", hashValue);
                    return new UrlNotFoundException(hashValue);
                });

        String originalUrl = url.getOriginalUrl();
        urlCacheRepository.saveUrl(hashValue, originalUrl);
        log.debug("Updated cache with database result: {} -> {}", hashValue, originalUrl);

        return originalUrl;
    }

    private void incrementVisitsAsync(String hashValue) {
        CompletableFuture.runAsync(() -> {
            try {
                urlVisitService.incrementVisits(hashValue);
                log.debug("Asynchronously incremented visit count for hash: {}", hashValue);
            } catch (Exception e) {
                log.error("Failed to increment visit count for hash: {}", hashValue, e);
            }
        });
    }

    private void validateUrl(String url) {
        if (!urlValidator.isValid(url)) {
            log.error("URL validation failed: {}", url);
            throw new InvalidUrlException("Invalid URL format: " + url);
        }
        log.debug("URL validation passed: {}", url);
    }

    private void validateHash(String hash) {
        if (!isValidHash(hash)) {
            log.error("Hash validation failed: {}", hash);
            throw new InvalidHashException("Invalid hash format: " + hash);
        }
        log.debug("Hash validation passed: {}", hash);
    }

    private boolean isValidHash(String hash) {
        return hash != null && hash.matches("^[a-zA-Z0-9]{6,8}$");
    }
}