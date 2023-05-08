package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final PostRepository postRepository;

    @Transactional
    public PostDto createPost(PostDto post) {
        postValidator.validateCreation(post);
        Post entity = postMapper.toEntity(post);
        return postMapper.toDto(postRepository.save(entity));
    }

    @Transactional
    public PostDto updatePost(PostDto post) {
        Post entity = findById(post.getId());
        postValidator.validateUpdate(entity, post);
        postMapper.update(entity, post);
        return postMapper.toDto(entity);
    }

    @Transactional
    public PostDto publishPost(long postId) {
        Post entity = findById(postId);
        if (entity.isPublished()) {
            throw new DataValidationException("Post with id " + postId + " has already been published");
        }
        entity.setPublished(true);
        entity.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostDrafts(long authorId) {
        return getDrafts(postRepository.findByAuthorId(authorId));
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPublicPosts(long authorId) {
        return getPublicPosts(postRepository.findByAuthorIdWithLikes(authorId));
    }

    @Transactional(readOnly = true)
    public List<PostDto> getProjectPostDrafts(long projectId) {
        return getDrafts(postRepository.findByProjectId(projectId));
    }

    @Transactional(readOnly = true)
    public List<PostDto> getProjectPublicPosts(long projectId) {
        return getPublicPosts(postRepository.findByProjectIdWithLikes(projectId));
    }

    private List<PostDto> getDrafts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getPublicPosts(List<Post> posts) {
        return posts.stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(post -> postMapper.toDtoWithLikes(post, post.getLikes().size()))
                .toList();
    }

    @Transactional(readOnly = true)
    public PostDto getPost(long id) {
        return postMapper.toDto(findById(id));
    }

    @Transactional
    public void softDeletePost(long id) {
        Post entity = findById(id);
        entity.setDeleted(true);
    }

    @Transactional
    public Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There's no post with id " + id));
    }
}
