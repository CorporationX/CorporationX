package faang.school.postservice.service.hashtag.cache;

import faang.school.postservice.entity.dto.post.PostHashtagDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashtagCacheServiceImplTest {

    @Mock
    private RedisTemplate<String, PostHashtagDto> redisTemplate;
    @Mock
    private ZSetOperations<String, PostHashtagDto> zSetOps;

    private HashtagCacheServiceImpl hashtagCacheService;

    private final String hashtag = "hashtag";
    private PostHashtagDto postDto;

    @BeforeEach
    void setUp() {

        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);

        postDto = PostHashtagDto.builder()
                .id(1L)
                .content("#hash #tag")
                .likesIds(List.of(1L, 2L, 3L))
                .build();

        hashtagCacheService = new HashtagCacheServiceImpl(redisTemplate);

        ReflectionTestUtils.setField(hashtagCacheService, "maxCacheSize", 1000L);
    }

    @Test
    void getPostsByHashtagInBounds() {
        Pageable pageable = PageRequest.of(0, 2);

        when(redisTemplate.hasKey(hashtag)).thenReturn(true);
        when(zSetOps.reverseRange(hashtag, pageable.getOffset(), pageable.getPageSize())).thenReturn(Set.of(postDto));

        Set<PostHashtagDto> actual = hashtagCacheService.getPostsByHashtag(hashtag, pageable);
        assertIterableEquals(Set.of(postDto), actual);

        InOrder inOrder = inOrder(redisTemplate, zSetOps);
        inOrder.verify(redisTemplate).hasKey(hashtag);
        inOrder.verify(zSetOps).reverseRange(hashtag, pageable.getOffset(), pageable.getPageSize());
    }

    @Test
    void getPostsByHashtagOutOfBounds() {
        Pageable pageable = PageRequest.of(999, 1001);

        Set<PostHashtagDto> actual = hashtagCacheService.getPostsByHashtag(hashtag, pageable);
        assertNull(actual);
    }

    @Test
    void getPostsByHashtagNoSuchKey() {
        Pageable pageable = PageRequest.of(0, 2);
        when(redisTemplate.hasKey(hashtag)).thenReturn(false);

        Set<PostHashtagDto> actual = hashtagCacheService.getPostsByHashtag(hashtag, pageable);
        assertNull(actual);

        InOrder inOrder = inOrder(redisTemplate, zSetOps);
        inOrder.verify(redisTemplate).hasKey(hashtag);
    }

    @Test
    void addPostToHashtag() {
        when(zSetOps.size(hashtag)).thenReturn(1000L);

        hashtagCacheService.addPostToHashtag(hashtag, postDto);

        InOrder inOrder = inOrder(redisTemplate, zSetOps);
        inOrder.verify(zSetOps).add(hashtag, postDto, postDto.getLikesIds().size());
        inOrder.verify(zSetOps).size(hashtag);
    }

    @Test
    void addPostToHashtagCacheOverflow() {
        when(zSetOps.size(hashtag)).thenReturn(1001L);

        hashtagCacheService.addPostToHashtag(hashtag, postDto);

        InOrder inOrder = inOrder(redisTemplate, zSetOps);
        inOrder.verify(zSetOps).add(hashtag, postDto, postDto.getLikesIds().size());
        inOrder.verify(zSetOps).size(hashtag);
        inOrder.verify(zSetOps).popMin(hashtag);
    }

    @Test
    void addPostToHashtagCacheNullSize() {
        when(zSetOps.size(hashtag)).thenReturn(null);

        hashtagCacheService.addPostToHashtag(hashtag, postDto);

        InOrder inOrder = inOrder(redisTemplate, zSetOps);
        inOrder.verify(zSetOps).add(hashtag, postDto, postDto.getLikesIds().size());
        inOrder.verify(zSetOps).size(hashtag);
    }

    @Test
    void removePostFromHashtag() {
        hashtagCacheService.removePostFromHashtag(hashtag, postDto);

        InOrder inOrder = inOrder(redisTemplate, zSetOps);
        inOrder.verify(zSetOps).remove(hashtag, postDto);
    }

    @Test
    void updateScore() {
        hashtagCacheService.updateScore(hashtag, postDto);

        InOrder inOrder = inOrder(redisTemplate, zSetOps);
        inOrder.verify(zSetOps).add(hashtag, postDto, postDto.getLikesIds().size());
    }
}