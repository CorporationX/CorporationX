package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostDto;
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

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
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
    private Post post;
    private PostDto postDto;

    @BeforeEach
    void init() {
        post = Post.builder()
                .id(1L)
                .content("#hash #tag")
                .build();

        postDto = PostDto.builder()
                .id(2L)
                .content("#hash #tag")
                .build();
    }

    @Test
    void getPostsByHashtag() {
        when(postMapper.toDto(post)).thenReturn(postDto);
        when(hashtagCacheService.getPostsByHashtag(hashtag1)).thenReturn(Set.of(post));

        List<PostDto> actual = hashtagService.getPostsByHashtag(hashtag1);
        assertIterableEquals(List.of(postDto), actual);

        InOrder inOrder = inOrder(hashtagRepository, postMapper, hashtagCacheService);
        inOrder.verify(hashtagCacheService).getPostsByHashtag(hashtag1);
        inOrder.verify(postMapper).toDto(post);
    }

    @Test
    void addHashtag() {
        hashtagService.addHashtag(hashtag1, post);

        InOrder inOrder = inOrder(hashtagRepository, hashtagCacheService, postMapper);
        inOrder.verify(hashtagRepository).save(any());
        inOrder.verify(hashtagCacheService).addPostToHashtag(hashtag1, post);
    }

    @Test
    void deleteHashtag() {
        hashtagService.deleteHashtag(hashtag1, post);

        InOrder inOrder = inOrder(hashtagRepository, hashtagCacheService, postMapper);
        inOrder.verify(hashtagRepository).deleteByHashtagAndPostId(anyString(), eq(post.getId()));
        inOrder.verify(hashtagCacheService).removePostFromHashtag(hashtag1, post);
    }
}