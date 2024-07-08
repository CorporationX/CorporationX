package faang.school.postservice.service.spelling;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SpellingService {

    CompletableFuture<Optional<String>> checkSpelling(String content);
}
