package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean
    public ExecutorService adRemoverExecutorService(@Value("${post.ad-remover.threads-count}") int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }
}
