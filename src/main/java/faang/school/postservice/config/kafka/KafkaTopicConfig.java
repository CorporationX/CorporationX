package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.data.channel.new_comment.name}")
    private String newCommentTopicName;

    @Bean
    public NewTopic newCommentTopic() {
        return new NewTopic(newCommentTopicName, 1, (short) 1);
    }
}
