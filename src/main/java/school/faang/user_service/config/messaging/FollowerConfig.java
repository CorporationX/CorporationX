package school.faang.user_service.config.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class FollowerConfig {

    @Value("${spring.data.redis.channel.follower}")
    private String channel;

    @Bean
    public ChannelTopic followerTopic() {
        return new ChannelTopic(channel);
    }
}
