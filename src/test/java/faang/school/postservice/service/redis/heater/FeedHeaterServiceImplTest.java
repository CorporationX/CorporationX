package faang.school.postservice.service.redis.heater;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisFeed;
import faang.school.postservice.event.heat.HeatUsersFeedEvent;
import faang.school.postservice.kafka.producer.HeatUsersFeedProducer;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedHeaterServiceImplTest {

    @InjectMocks
    private FeedHeaterServiceImpl feedHeaterService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private HeatUsersFeedProducer producer;

    @Mock
    private RedisFeedRepository redisFeedRepository;

    @Mock
    private PostService postService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedHeaterService, "feedTtl", 60000L);
        ReflectionTestUtils.setField(feedHeaterService, "feedHeatSize", 100);
    }

    @Test
    void testFeedHeat() {
        HashSet<Long> userIds = new HashSet<>(List.of(1L, 2L, 3L));

        when(userServiceClient.getAllUsersIds()).thenReturn(userIds);

        feedHeaterService.feedHeat();

        verify(userServiceClient).getAllUsersIds();
        verify(feedHeaterService, times(1)).sendUsersBatch(userIds);
    }

    @Test
    void testSendUsersBatch() {
        Set<Long> userIds = new HashSet<>(List.of(1L, 2L, 3L, 4L));

        doNothing().when(producer).publish(any(HeatUsersFeedEvent.class));

        feedHeaterService.sendUsersBatch(userIds);

        verify(producer, times(1)).publish(any(HeatUsersFeedEvent.class));
    }

    @Test
    void testGenerateUsersFeed() {
        HashSet<Long> userIds = new HashSet<>(List.of(1L, 2L, 3L));
        HeatUsersFeedEvent event = new HeatUsersFeedEvent(userIds);

        doNothing().when(feedHeaterService).buildAndSaveNewRedisFeed(any(Long.class));

        feedHeaterService.generateUsersFeed(event);

        verify(feedHeaterService, times(userIds.size())).buildAndSaveNewRedisFeed(any(Long.class));
    }

    @Test
    void testBuildAndSaveNewRedisFeed() {
        Long userId = 1L;
        Set<Long> postIds = new HashSet<>(List.of(1L, 2L, 3L));
        List<PostDto> posts = postIds.stream().map(id -> PostDto.builder()
                .id(id)
                .content("Content")
                .createdAt(LocalDateTime.now())
                .build()).toList();
        LinkedHashSet<Long> postIdsSet = new LinkedHashSet<>(postIds);

        when(postService.findUserFollowingsPosts(userId, LocalDateTime.now(), 500)).thenReturn(posts);

        RedisFeed result = feedHeaterService.buildAndSaveNewRedisFeed(userId);

        verify(redisFeedRepository).save(any(Long.class), any(RedisFeed.class));
        assertEquals(userId, result.getUserId());
        assertEquals(postIdsSet, result.getPostsIds());
    }
}
