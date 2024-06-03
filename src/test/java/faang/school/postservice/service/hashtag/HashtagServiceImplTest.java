package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashtagServiceImplTest {

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private HashtagServiceImpl hashtagService;

    private final String hashtag1 = "hash";
    private Post post;
    private Hashtag hashtag;
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

        hashtag = Hashtag.builder()
                .id(3L)
                .hashtag(hashtag1)
                .post(post)
                .build();
    }

    @Test
    void getHashtags() {
        String hashtag2 = "tag";
        Set<String> expected = Set.of(hashtag1, hashtag2);
        Set<String> actual = hashtagService.getHashtags(post.getContent());
        assertIterableEquals(expected, actual);
    }

    @Test
    void getPostsByHashtag() {
        when(hashtagRepository.findAllByHashtag(hashtag1)).thenReturn(List.of(hashtag));
        when(postMapper.toDto(post)).thenReturn(postDto);

        List<PostDto> actual = hashtagService.getPostsByHashtag(hashtag1);
        assertIterableEquals(List.of(postDto), actual);

        InOrder inOrder = inOrder(hashtagRepository, postMapper);
        inOrder.verify(hashtagRepository).findAllByHashtag(hashtag1);
        inOrder.verify(postMapper).toDto(post);
    }

    @Test
    void addHashtags() {
        hashtagService.addHashtags(post);

        InOrder inOrder = inOrder(hashtagRepository);
        inOrder.verify(hashtagRepository, times(2)).save(any());
    }

    @Test
    void deleteHashtags() {
        hashtagService.deleteHashtags(post);

        InOrder inOrder = inOrder(hashtagRepository);
        inOrder.verify(hashtagRepository, times(2)).deleteByHashtagAndPostId(any(), eq(post.getId()));
    }

    @Test
    void updateHashtags() {
        post.setContent("#new");

        when(hashtagRepository.findAllByPostId(post.getId())).thenReturn(List.of(hashtag));

        hashtagService.updateHashtags(post);

        InOrder inOrder = inOrder(hashtagRepository);
        inOrder.verify(hashtagRepository).deleteByHashtagAndPostId(any(), eq(post.getId()));
        inOrder.verify(hashtagRepository).save(any());
    }
}