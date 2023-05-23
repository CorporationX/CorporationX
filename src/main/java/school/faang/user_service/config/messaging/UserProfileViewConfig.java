package school.faang.user_service.config.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class UserProfileViewConfig {

    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String channel;

    @Bean
    public ChannelTopic profileViewTopic() {
        return new ChannelTopic(channel);
    }
}
