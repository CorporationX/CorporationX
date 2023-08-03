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
}
