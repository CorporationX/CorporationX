package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import faang.school.postservice.repository.PostRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;

    @Test
    void testCreateDraftPostValidData() {
        PostDto expectedDto = PostDto.builder()
                .content("Content")
                .userId(1L)
                .build();
        Post post = Post.builder()
                .content("Content")
                .authorId(1L)
                .build();

        when(postMapper.toEntity(expectedDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(expectedDto);

        PostDto actualDto = postService.createDraftPost(expectedDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        assertEquals(1L, actualDto.getUserId());
    }

    @Test
    void testCreateDraftPostValidateId() {
        PostDto postDto = PostDto.builder()
                .content("Content")
                .userId(1L)
                .projectId(2L)
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> postService.createDraftPost(postDto));
        assertEquals("Enter one thing: authorId or projectId", exception.getMessage());
    }

    @Test
    void testCreateDraftPostValidateUserExist() {
        PostDto postDto = PostDto.builder()
                .content("Content")
                .userId(1L)
                .build();

        doThrow(FeignException.class).when(userServiceClient).getUser(1L);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(postDto));
        assertEquals("User with the specified authorId does not exist", exception.getMessage());
    }

    @Test
    void testCreateDraftPostValidateProjectExist() {
        PostDto postDto = PostDto.builder()
                .content("Content")
                .projectId(1L)
                .build();

        doThrow(FeignException.class).when(projectServiceClient).getProject(1L);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(postDto));
        assertEquals("Project with the specified projectId does not exist", exception.getMessage());
    }
}
