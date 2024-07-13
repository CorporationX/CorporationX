package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.data.channel.new_comment.name}")
    private String newCommentTopicName;

    @Value("${spring.data.channel.post_view.name}")
    private String postViewTopicName;

    @Value("${spring.data.channel.new_post.name}")
    private String newPostTopicName;

    @Bean
    public NewTopic newCommentTopic() {
        return new NewTopic(newCommentTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic postViewTopic() {
        return new NewTopic(postViewTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic newPostTopic() {
        return new NewTopic(newPostTopicName, 1, (short) 1);
    }
}
