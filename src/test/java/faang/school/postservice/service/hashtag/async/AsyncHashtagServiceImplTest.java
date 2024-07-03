package faang.school.postservice.service.hashtag.async;

import faang.school.postservice.dto.post.PostHashtagDto;
import faang.school.postservice.service.hashtag.HashtagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
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

    private PostHashtagDto post;

    @BeforeEach
    void setUp() {

        post = PostHashtagDto.builder()
                .id(2L)
                .content("#hash #tag")
                .build();
    }

    @Test
    void getPostsByHashtag() {
        String hashtag1 = "hash";

        when(hashtagService.getPageOfPostsByHashtag(eq(hashtag1), any(Pageable.class))).thenReturn(List.of(post));

        List<PostHashtagDto> actual = hashtagService.getPageOfPostsByHashtag(hashtag1, Pageable.ofSize(1));
        assertIterableEquals(List.of(post), actual);

        InOrder inOrder = inOrder(hashtagService);
        inOrder.verify(hashtagService).getPageOfPostsByHashtag(eq(hashtag1), any(Pageable.class));
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

    @Test
    void updateScore() {
        asyncHashtagService.updateScore(post);

        InOrder inOrder = inOrder(hashtagService);
        inOrder.verify(hashtagService, times(2)).updateScore(anyString(), eq(post));
    }
}