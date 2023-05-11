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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final int POST_BATCH_SIZE = 1000;

    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final ThreadPoolExecutor postPublisherExecutor;

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
        Post post = findById(postId);
        if (post.isPublished()) {
            throw new DataValidationException("Post with id " + postId + " has already been published");
        }
        publish(post);
        return postMapper.toDto(post);
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

    @Transactional
    public void publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();
        for (int i = 0; i < posts.size(); i += POST_BATCH_SIZE) {
            int toIndex = Math.min(i + POST_BATCH_SIZE, posts.size());
            List<Post> batch = posts.subList(i, toIndex);
            CompletableFuture.runAsync(() -> {
                batch.forEach(this::publish);
                postRepository.saveAll(batch);
            }, postPublisherExecutor);
        }
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

    @Transactional(readOnly = true)
    public Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There's no post with id " + id));
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
                .map(post -> {
                    PostDto dto = postMapper.toDto(post);
                    dto.setLikes(post.getLikes().size());
                    return dto;
                })
                .toList();
    }

    private void publish(Post post) {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
    }
}
