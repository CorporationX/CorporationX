package faang.school.urlshortenerservice.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

@Component
@Slf4j
public class UrlValidator {
    private final Pattern urlPattern;
    private final List<String> allowedProtocols;
    private final int maxLength;

    public UrlValidator(
            @Value("${url.validation.allowed-protocols}") List<String> allowedProtocols,
            @Value("${url.validation.max-length}") int maxLength) {
        this.allowedProtocols = allowedProtocols;
        this.maxLength = maxLength;
        this.urlPattern = Pattern.compile(
                "^(https?://)" +                // протокол
                        "([\\w\\d-]+\\.)*[\\w\\d-]+\\." + // поддомены
                        "[a-z]{2,}" +                   // домен верхнего уровня
                        "(:\\d{1,5})?" +                // порт (опционально)
                        "(/[\\w\\d\\-./]*)*" +          // путь
                        "(\\?[^#]*)?" +                 // query параметры
                        "(#.*)?$",                      // fragment
                Pattern.CASE_INSENSITIVE
        );
    }

    public boolean isValid(String url) {
        if (url == null || url.isBlank()) {
            log.debug("URL is null or blank");
            return false;
        }

        if (url.length() > maxLength) {
            log.debug("URL exceeds maximum length of {} characters", maxLength);
            return false;
        }

        try {
            URL parsedUrl = new URL(url);

            // Проверяем протокол
            if (!allowedProtocols.contains(parsedUrl.getProtocol())) {
                log.debug("URL protocol '{}' is not allowed. Allowed protocols: {}",
                        parsedUrl.getProtocol(), allowedProtocols);
                return false;
            }

            // Проверяем по регулярному выражению
            if (!urlPattern.matcher(url).matches()) {
                log.debug("URL does not match required pattern: {}", url);
                return false;
            }

            return true;
        } catch (MalformedURLException e) {
            log.debug("Invalid URL format: {}", url, e);
            return false;
        }
    }
}