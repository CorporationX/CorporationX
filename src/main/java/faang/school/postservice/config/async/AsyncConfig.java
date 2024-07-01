package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${async.corePoolSize}")
    private int corePoolSize;

    @Value("${async.maxPoolSize}")
    private int maxPoolSize;

    @Value("${async.queueCapacity}")
    private int queueCapacity;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ExecutorService adRemoverExecutorService(@Value("${post.ad-remover.threads-count}") int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }

    @Bean
    public ExecutorService commentModeratorExecutorService(@Value("${moderation.threads-count}") int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }
}
