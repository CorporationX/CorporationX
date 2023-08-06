package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional
    public PostDto createDraftPost(PostDto postDto) {
        validateIdPostDto(postDto);
        validateAuthorExist(postDto);

        Post post = postMapper.toEntity(postDto);

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto publishPost(Long id) {
        Post post = validatePostExist(id);

        if (post.isPublished() || post.isDeleted()) {
            throw new DataValidationException("Post is already published or deleted");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        validateIdPostDto(postDto);
        validateAuthorExist(postDto);
        Post post = validatePostExist(postDto.getId());

        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return postMapper.toDto(post);
    }

    private void validateIdPostDto(PostDto postDto) {
        if ((postDto.getAuthorId() == null && postDto.getProjectId() == null) ||
                (postDto.getAuthorId() != null && postDto.getProjectId() != null)) {
            throw new DataValidationException("Enter one thing: authorId or projectId");
        }
    }

    private void validateAuthorExist(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            try {
                userServiceClient.getUser(postDto.getAuthorId());
            } catch (FeignException e) {
                throw new EntityNotFoundException("User with the specified authorId does not exist");
            }
        } else if (postDto.getProjectId() != null) {
            try {
                projectServiceClient.getProject(postDto.getProjectId());
            } catch (FeignException e) {
                throw new EntityNotFoundException("Project with the specified projectId does not exist");
            }
        }
    }

    private Post validatePostExist(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with the specified id does not exist"));
    }
}
