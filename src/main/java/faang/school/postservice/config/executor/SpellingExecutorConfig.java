package faang.school.postservice.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class SpellingExecutorConfig {

    @Bean
    public Executor spellingExecutor(@Value("${spelling.threads}") int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }
}
