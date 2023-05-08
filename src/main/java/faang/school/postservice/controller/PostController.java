package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    public PostDto createPost(@RequestBody @Valid PostDto post) {
        if (validate(post)) {
            return postService.createPost(post);
        }
        throw new DataValidationException("Invalid post provided");
    }

    @PutMapping("/post")
    public PostDto updatePost(@RequestBody @Valid PostDto post) {
        if (validate(post)) {
            return postService.updatePost(post);
        }
        throw new DataValidationException("Invalid post provided");
    }

    @PutMapping("/post/{postId}/public")
    public PostDto publishPost(@PathVariable long postId) {
        return postService.publishPost(postId);
    }

    @GetMapping("/user/{userId}/post/draft")
    public List<PostDto> getPostDrafts(@PathVariable long userId) {
        return postService.getPostDrafts(userId);
    }

    @GetMapping("/project/{projectId}/post/draft")
    public List<PostDto> getProjectPostDrafts(@PathVariable long projectId) {
        return postService.getProjectPostDrafts(projectId);
    }

    @GetMapping("/project/{projectId}/post/public")
    public List<PostDto> getProjectPublicPosts(@PathVariable long projectId) {
        return postService.getProjectPublicPosts(projectId);
    }

    @GetMapping("/user/{userId}/post/public")
    public List<PostDto> getPublicPosts(@PathVariable long userId) {
        return postService.getPublicPosts(userId);
    }

    @GetMapping("/post/{postId}")
    public PostDto getPost(@PathVariable long postId) {
        return postService.getPost(postId);
    }

    @DeleteMapping("/post/{postId}")
    public void deletePost(@PathVariable long postId) {
        postService.softDeletePost(postId);
    }

    private boolean validate(PostDto post) {
        return post.getAuthorId() != null || post.getProjectId() != null;
    }
}
