package faang.school.postservice.service.resource;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.s3.AmazonS3Service;
import faang.school.postservice.validator.resource.ResourceValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;
    @Spy
    private ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);
    @Mock
    private ResourceValidator resourceValidator;
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private PostService postService;

    @InjectMocks
    private ResourceServiceImpl resourceServiceImpl;

    @Test
    void successFindById() {
        Resource resource = Resource.builder().id(1L).build();

        when(resourceRepository.findById(1L)).thenReturn(Optional.ofNullable(resource));

        Resource result = resourceServiceImpl.findById(1L);

        assertEquals(resource, result);
    }

    @Test
    void successCreate() {
        MultipartFile file = mock(MultipartFile.class);
        Post post = Post.builder().id(1L).build();
        String key = UUID.randomUUID().toString();

        when(postService.findById(1L)).thenReturn(post);
        when(amazonS3Service.uploadFile(file)).thenReturn(key);
        when(resourceRepository.save(any(Resource.class))).thenAnswer(i -> i.getArguments()[0]);

        List<ResourceDto> result = resourceServiceImpl.create(1L, List.of(file));
        assertEquals(key, result.get(0).getKey());
    }

    @Test
    void successDownloadResource() {
        S3ObjectInputStream inputStreamMock = mock(S3ObjectInputStream.class);

        when(amazonS3Service.downloadFile(anyString())).thenReturn(inputStreamMock);

        InputStream result = resourceServiceImpl.downloadResource(anyString());

        assertEquals(inputStreamMock, result);
    }

    @Test
    void successDeleteFile() {
        String key = "test";
        resourceServiceImpl.deleteFile(key);

        verify(resourceValidator, times(1)).validateExistenceByKey(key);
        verify(resourceRepository, times(1)).deleteByKey(key);
        verify(amazonS3Service, times(1)).deleteFile(key);
    }
}
