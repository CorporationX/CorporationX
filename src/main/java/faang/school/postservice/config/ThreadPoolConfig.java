package faang.school.postservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "postPublisherThreadPool")
    public ThreadPoolExecutor postPublisherThreadPool() {
        return new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    @Bean(name = "adRemoverThreadPool")
    public ThreadPoolExecutor adRemoverThreadPool() {
        return new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }
}
