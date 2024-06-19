package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostHashtagDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.service.hashtag.cache.HashtagCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashtagServiceImplTest {

    @Mock
    private HashtagRepository hashtagRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private HashtagCacheService hashtagCacheService;

    @InjectMocks
    private HashtagServiceImpl hashtagService;

    private final String hashtag1 = "hash";
    private PostHashtagDto postDto;
    private Post post;

    @BeforeEach
    void init() {
        postDto = PostHashtagDto.builder()
                .id(1L)
                .content("#hash #tag")
                .build();

        post = Post.builder()
                .id(1L)
                .content("#hash #tag")
                .build();
    }

    @Test
    void addHashtag() {
        when(postMapper.toEntity(postDto)).thenReturn(post);

        hashtagService.addHashtag(hashtag1, postDto);

        InOrder inOrder = inOrder(hashtagRepository, hashtagCacheService, postMapper);
        inOrder.verify(hashtagRepository).save(any());
        inOrder.verify(hashtagCacheService).addPostToHashtag(hashtag1, postDto);
    }

    @Test
    void deleteHashtag() {
        hashtagService.deleteHashtag(hashtag1, postDto);

        InOrder inOrder = inOrder(hashtagRepository, hashtagCacheService, postMapper);
        inOrder.verify(hashtagRepository).deleteByHashtagAndPostId(anyString(), eq(postDto.getId()));
        inOrder.verify(hashtagCacheService).removePostFromHashtag(hashtag1, postDto);
    }

    @Test
    void updateHashtagExists() {
        when(hashtagRepository.existsByHashtag(hashtag1)).thenReturn(true);

        hashtagService.updateHashtag(hashtag1, postDto);

        InOrder inOrder = inOrder(hashtagRepository, hashtagCacheService, postMapper);
        inOrder.verify(hashtagRepository).existsByHashtag(hashtag1);
        inOrder.verify(hashtagRepository).deleteByHashtagAndPostId(anyString(), eq(postDto.getId()));
        inOrder.verify(hashtagCacheService).removePostFromHashtag(hashtag1, postDto);
    }

    @Test
    void updateHashtagNotExists() {
        when(hashtagRepository.existsByHashtag(hashtag1)).thenReturn(false);
        when(postMapper.toEntity(postDto)).thenReturn(post);

        hashtagService.updateHashtag(hashtag1, postDto);

        InOrder inOrder = inOrder(hashtagRepository, hashtagCacheService, postMapper);
        inOrder.verify(hashtagRepository).existsByHashtag(hashtag1);
        inOrder.verify(hashtagRepository).save(any());
        inOrder.verify(hashtagCacheService).addPostToHashtag(hashtag1, postDto);
    }

    @Test
    void updateScore() {
        hashtagService.updateScore(hashtag1, postDto);

        InOrder inOrder = inOrder(hashtagRepository, hashtagCacheService, postMapper);
        inOrder.verify(hashtagCacheService).updateScore(hashtag1, postDto);
    }
}