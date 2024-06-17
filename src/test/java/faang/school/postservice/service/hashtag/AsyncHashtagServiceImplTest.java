package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.hashtag.async.AsyncHashtagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncHashtagServiceImplTest {

    @Mock
    private HashtagService hashtagService;

    @InjectMocks
    private AsyncHashtagServiceImpl asyncHashtagService;

    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
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
        String hashtag1 = "hash";

        when(hashtagService.getPostsByHashtag(hashtag1)).thenReturn(List.of(postDto));

        List<PostDto> actual = hashtagService.getPostsByHashtag(hashtag1);
        assertIterableEquals(List.of(postDto), actual);

        InOrder inOrder = inOrder(hashtagService);
        inOrder.verify(hashtagService).getPostsByHashtag(hashtag1);
    }

    @Test
    void addHashtags() {
        asyncHashtagService.addHashtags(post);

        InOrder inOrder = inOrder(hashtagService);
        inOrder.verify(hashtagService, times(2)).addHashtag(anyString(), eq(post));
    }

    @Test
    void removeHashtags() {
        asyncHashtagService.removeHashtags(post);

        InOrder inOrder = inOrder(hashtagService);
        inOrder.verify(hashtagService, times(2)).deleteHashtag(anyString(), eq(post));
    }

    @Test
    void updateHashtags() {
        asyncHashtagService.updateHashtags(post);

        InOrder inOrder = inOrder(hashtagService);
        inOrder.verify(hashtagService, times(2)).updateHashtag(anyString(), eq(post));
    }
}