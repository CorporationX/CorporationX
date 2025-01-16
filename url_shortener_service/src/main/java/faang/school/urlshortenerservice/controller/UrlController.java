package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlShortenRequest;
import faang.school.urlshortenerservice.dto.UrlShortenResponse;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<UrlShortenResponse> shortenUrl(
            @Valid @RequestBody UrlShortenRequest request) {
        log.debug("Received request to shorten URL: {}", request.getUrl());
        String shortUrl = urlService.createShortUrl(request.getUrl());
        return ResponseEntity.ok(new UrlShortenResponse(shortUrl));
    }

    @GetMapping("/{hashValue}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hashValue) {
        log.debug("Received request to redirect hash: {}", hashValue);
        return urlService.redirect(hashValue);
    }
}