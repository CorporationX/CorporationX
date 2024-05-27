package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AdRemoverAsyncConfig {

    @Value("${post.ad-remover.threads-count}")
    private int threadsCount;

    @Bean
    public ExecutorService adRemoverExecutorService() {
        return Executors.newFixedThreadPool(threadsCount);
    }
}
