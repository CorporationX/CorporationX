package faang.school.postservice.service.resource;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.exception.S3Exception;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.s3.AmazonS3Service;
import faang.school.postservice.validator.resource.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ResourceValidator resourceValidator;
    private final AmazonS3Service amazonS3Service;
    private final PostService postService;

    @Override
    @Transactional
    public Resource findById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Resource with id %s not found", id)));
    }

    @Override
    @Transactional
    public List<ResourceDto> create(Long postId, List<MultipartFile> files) {

        Post post = postService.findById(postId);
        resourceValidator.validateCountFilesPerPost(postId, files.size());

        try(ExecutorService executorService = Executors.newFixedThreadPool(files.size())) {
            List<CompletableFuture<Resource>> resources = new ArrayList<>();
            List<Resource> savedResources = new ArrayList<>();

            files.forEach(file -> {
                CompletableFuture<Resource> resource = CompletableFuture.supplyAsync(() -> {
                    String key = amazonS3Service.uploadFile(file);
                    return Resource.builder()
                            .name(file.getOriginalFilename())
                            .key(key)
                            .size(file.getSize())
                            .type(file.getContentType())
                            .post(post)
                            .build();
                }, executorService);
                resources.add(resource);
            });

            resources.forEach(resource -> {
                Resource resourceToSave = resource.join();
                savedResources.add(resourceRepository.save(resourceToSave));
            });

            return savedResources.stream()
                    .map(resourceMapper::toDto)
                    .toList();

        } catch (AmazonS3Exception ex) {
            throw new S3Exception(ex.getMessage());
        }
    }

    @Override
    public InputStream downloadResource(String key) {
        resourceValidator.validateExistenceByKey(key);
        return amazonS3Service.downloadFile(key);
    }

    @Override
    @Transactional
    public void deleteFile(String key) {
        resourceValidator.validateExistenceByKey(key);
        resourceRepository.deleteByKey(key);
        amazonS3Service.deleteFile(key);
    }
}
