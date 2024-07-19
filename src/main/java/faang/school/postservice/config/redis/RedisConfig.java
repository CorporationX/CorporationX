package faang.school.postservice.config.redis;

import faang.school.postservice.entity.dto.post.PostHashtagDto;
import faang.school.postservice.entity.model.redis.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(basePackages = "faang.school.postservice.repository.redis")
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    private <T> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory redisConnectionFactory, Class<T> clazz) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, PostHashtagDto> postHashtagRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory, PostHashtagDto.class);
    }

    @Bean
    public RedisTemplate<String, RedisFeed> redisFeedTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory, RedisFeed.class);
    }

    @Bean
    public RedisTemplate<String, RedisComment> redisCommentsTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory, RedisComment.class);
    }

    @Bean
    public RedisTemplate<String, RedisCommentLike> redisLikesTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory, RedisCommentLike.class);
    }

    @Bean
    public RedisTemplate<String, RedisUser> redisUsersTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory, RedisUser.class);
    }

    @Bean
    public RedisTemplate<String, RedisPost> redisPostsTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory, RedisPost.class);
    }

    @Bean
    public RedisTemplate<String, Object> genericRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory, Object.class);
    }

    @Bean
    public ChannelTopic likeTopic(@Value("${spring.data.channel.like_post_channel.name}") String topicName) {
        return new ChannelTopic(topicName);
    }

    @Bean
    public ZSetOperations<String, RedisFeed> redisFeedZSetOps(@Qualifier("redisFeedTemplate") RedisTemplate<String, RedisFeed> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public ZSetOperations<String, RedisComment> redisCommentZSetOps(@Qualifier("redisCommentsTemplate") RedisTemplate<String, RedisComment> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public ZSetOperations<String, RedisCommentLike> redisLikeZSetOps(@Qualifier("redisLikesTemplate") RedisTemplate<String, RedisCommentLike> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public ZSetOperations<String, RedisPost> redisPostZSetOps(@Qualifier("redisPostsTemplate") RedisTemplate<String, RedisPost> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public ZSetOperations<String, RedisUser> redisUserZSetOps(@Qualifier("redisUsersTemplate") RedisTemplate<String, RedisUser> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}