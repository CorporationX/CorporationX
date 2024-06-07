package faang.school.postservice.service.spelling;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.json.SpellingCorrector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpellingServiceImpl implements SpellingService{

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spelling.url}")
    private String url;

    @Async("spellingExecutor")
    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 3))
    public CompletableFuture<Optional<String>> checkSpelling(String content) {
        SpellingCorrector[] spellingCorrectors;
        byte[] spellingResponse = restTemplate.getForObject(url + content, byte[].class);
        try {
            spellingCorrectors = objectMapper.readValue(spellingResponse, SpellingCorrector[].class);
        } catch (IOException e) {
            log.error("Error converting JSON to POJO", e);
            throw new RuntimeException("Error converting JSON to POJO", e);
        }

        if(spellingCorrectors.length == 0){
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.completedFuture(Optional.of(correct(content, spellingCorrectors)));
    }

    private String correct(String content, SpellingCorrector[] spellingCorrectors){
        AtomicReference<String> futureCorrectedContent = new AtomicReference<>(content);
        Arrays.stream(spellingCorrectors)
                .forEach(spell-> futureCorrectedContent.set(futureCorrectedContent.get().replaceFirst(spell.getWord(), spell.getPossibleVariants()[0])));
        return futureCorrectedContent.get();
    }
}
