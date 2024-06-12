package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.HashtagMapper;
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
    private HashtagMapper hashtagMapper;

    @InjectMocks
    private HashtagServiceImpl hashtagService;

    private final String hashtag1 = "hash";
    private Post post;
    private Hashtag hashtag;
    private PostDto postDto;
    private HashtagDto hashtagDto;

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

        hashtagDto = HashtagDto.builder()
                .id(hashtag.getId())
                .hashtag(hashtag1)
                .postId(post.getId())
                .build();
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
    void getHashtagsByPost() {
        when(hashtagRepository.findAllByPostId(post.getId())).thenReturn(List.of(hashtag));
        when(hashtagMapper.toDto(hashtag)).thenReturn(hashtagDto);

        List<HashtagDto> actual = hashtagService.getHashtagsByPostId(post.getId());
        assertIterableEquals(List.of(hashtagDto), actual);

        InOrder inOrder = inOrder(hashtagRepository, hashtagMapper);
        inOrder.verify(hashtagRepository).findAllByPostId(post.getId());
        inOrder.verify(hashtagMapper).toDto(hashtag);
    }

    @Test
    void addHashtag() {
        hashtagService.addHashtag(hashtag1, post);

        InOrder inOrder = inOrder(hashtagRepository);
        inOrder.verify(hashtagRepository).save(any());
    }

    @Test
    void deleteHashtag() {
        hashtagService.deleteHashtag(hashtag1, post);

        InOrder inOrder = inOrder(hashtagRepository);
        inOrder.verify(hashtagRepository).deleteByHashtagAndPostId(anyString(), eq(post.getId()));
    }
}