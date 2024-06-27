package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);
    @Mock
    private PostValidator postValidator;

    @InjectMocks
    private PostServiceImpl postServiceImpl;

    @Test
    void successCreate() {
        PostCreateDto postCreateDto = new PostCreateDto("test", 1L, 0L);
        PostDto expectedResult = PostDto.builder()
                .content(postCreateDto.getContent())
                .authorId(postCreateDto.getAuthorId())
                .projectId(postCreateDto.getProjectId())
                .build();
        Post post = postMapper.toEntity(postCreateDto);

        doNothing().when(postValidator).validateAuthor(postCreateDto.getAuthorId(), postCreateDto.getProjectId());
        doNothing().when(postValidator).validatePostContent(postCreateDto.getContent());
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postServiceImpl.create(postCreateDto);
        assertEquals(expectedResult, result);
    }

    @Test
    void successPublish() {
        Post post = Post.builder()
                .id(1L)
                .content("test")
                .published(false)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postServiceImpl.publish(1L);
        assertTrue(result.isPublished());
    }

    @Test
    void successUpdate() {
        Post post = Post.builder()
                .id(1L)
                .content("test")
                .published(false)
                .build();
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postServiceImpl.update(1L, new PostUpdateDto("new"));

        assertEquals("new", result.getContent());
    }

    @Test
    void successDeleteById() {
        Post post = Post.builder()
                .id(1L)
                .content("test")
                .published(false)
                .build();
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        postServiceImpl.deleteById(1L);

        assertTrue(post.isDeleted());
    }

    @Test
    void successFindPostDraftsByUserAuthorId() {
        List<Post> posts = getPosts();
        List<PostDto> expectedResult = getPosts().stream()
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();

        when(postRepository.findByAuthorIdAndPublishedAndDeletedWithLikes(1L, false, false))
                .thenReturn(posts);

        List<PostDto> result = postServiceImpl.findPostDraftsByUserAuthorId(1L);

        assertEquals(
                expectedResult.stream().map(PostDto::getId).toList(),
                result.stream().map(PostDto::getId).toList()
        );
    }

    @Test
    void successFindPostDraftsByProjectAuthorId() {
        List<Post> posts = getPosts();
        List<PostDto> expectedResult = getPosts().stream()
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();

        when(postRepository.findByProjectIdAndPublishedAndDeletedWithLikes(1L, false, false))
                .thenReturn(posts);

        List<PostDto> result = postServiceImpl.findPostDraftsByProjectAuthorId(1L);

        assertEquals(
                expectedResult.stream().map(PostDto::getId).toList(),
                result.stream().map(PostDto::getId).toList()
        );
    }

    @Test
    void successFindPostPublicationByUserAuthorId() {
        List<Post> posts = getPosts();
        List<PostDto> expectedResult = getPosts().stream()
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .toList();

        when(postRepository.findByAuthorIdAndPublishedAndDeletedWithLikes(1L, true, false))
                .thenReturn(posts);

        List<PostDto> result = postServiceImpl.findPostPublicationsByUserAuthorId(1L);

        assertEquals(
                expectedResult.stream().map(PostDto::getId).toList(),
                result.stream().map(PostDto::getId).toList()
        );
    }

    @Test
    void successFindPostPublicationByProjectAuthorId() {
        List<Post> posts = getPosts();
        List<PostDto> expectedResult = getPosts().stream()
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .toList();

        when(postRepository.findByProjectIdAndPublishedAndDeletedWithLikes(1L, true, false))
                .thenReturn(posts);

        List<PostDto> result = postServiceImpl.findPostPublicationsByProjectAuthorId(1L);

        assertEquals(
                expectedResult.stream().map(PostDto::getId).toList(),
                result.stream().map(PostDto::getId).toList()
        );
    }

    private List<Post> getPosts() {
        return new ArrayList<>(List.of(
                Post.builder().id(1L).createdAt(LocalDateTime.now().minusDays(1)).publishedAt(LocalDateTime.now().minusDays(1)).build(),
                Post.builder().id(2L).createdAt(LocalDateTime.now().minusDays(1)).publishedAt(LocalDateTime.now().minusDays(2)).build(),
                Post.builder().id(3L).createdAt(LocalDateTime.now().minusDays(2)).publishedAt(LocalDateTime.now().minusDays(13)).build(),
                Post.builder().id(4L).createdAt(LocalDateTime.now().minusDays(2)).publishedAt(LocalDateTime.now().minusDays(2)).build(),
                Post.builder().id(5L).createdAt(LocalDateTime.now().minusDays(6)).publishedAt(LocalDateTime.now().minusDays(3)).build(),
                Post.builder().id(6L).createdAt(LocalDateTime.now().minusDays(5)).publishedAt(LocalDateTime.now().minusDays(4)).build(),
                Post.builder().id(7L).createdAt(LocalDateTime.now().minusDays(2)).publishedAt(LocalDateTime.now().minusDays(5)).build(),
                Post.builder().id(8L).createdAt(LocalDateTime.now().minusDays(2)).publishedAt(LocalDateTime.now().minusDays(4)).build(),
                Post.builder().id(9L).createdAt(LocalDateTime.now().minusDays(4)).publishedAt(LocalDateTime.now().minusDays(3)).build()
        ));
    }
}