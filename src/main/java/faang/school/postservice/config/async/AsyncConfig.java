package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor(
            @Value("${async.task.corePoolSize}") int corePoolSize,
            @Value("${async.task.maxPoolSize}") int maxPoolSize,
            @Value("${async.task.queueCapacity}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("AsyncTaskThread-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "postRemoveOrAddExecutor")
    public Executor postRemoveExecutor(
            @Value("${async.post.corePoolSize}") int corePoolSize,
            @Value("${async.post.maxPoolSize}") int maxPoolSize,
            @Value("${async.post.queueCapacity}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("PostRemoveOrAddThread-");
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