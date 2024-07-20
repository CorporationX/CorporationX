package faang.school.postservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.property.AmazonS3Properties;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
@EnableRedisRepositories
@EnableFeignClients(basePackages = "faang.school.postservice.client")
@EnableConfigurationProperties(AmazonS3Properties.class)
@EnableRetry
public class PostServiceApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(PostServiceApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
