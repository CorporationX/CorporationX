package faang.school.postservice.controller;

import faang.school.postservice.entity.dto.post.PostCreateDto;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @GetMapping("{postId}")
    public PostDto findById(@PathVariable Long postId) {
        return postMapper.toDto(postService.findById(postId));
    }

    @PostMapping
    public PostDto create(@RequestBody @Valid PostCreateDto postCreateDto) {
        return postService.create(postCreateDto);
    }

    @PostMapping("publication/{postId}")
    public PostDto publish(@PathVariable Long postId) {
        return postService.publish(postId);
    }

    @PatchMapping("{postId}")
    public PostDto update(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateDto postUpdateDto
    ) {
        return postService.update(postId, postUpdateDto);
    }

    @DeleteMapping("{postId}")
    public void deleteById(@PathVariable Long postId) {
        postService.deleteById(postId);
    }

    @GetMapping("drafts-by-user/{userId}")
    public List<PostDto> findAllPostDraftsByAuthorId(@PathVariable Long userId) {
        return postService.findPostDraftsByUserAuthorId(userId);
    }

    @GetMapping("drafts-by-project/{projectId}")
    public List<PostDto> findAllPostDraftsByProjectId(@PathVariable Long projectId) {
        return postService.findPostDraftsByProjectAuthorId(projectId);
    }

    @GetMapping("publication-by-user/{userId}")
    public List<PostDto> findAllPostPublicationByAuthorId(@PathVariable Long userId) {
        return postService.findPostPublicationsByUserAuthorId(userId);
    }

    @GetMapping("publication-by-project/{projectId}")
    public List<PostDto> findAllPostPublicationByProjectId(@PathVariable Long projectId) {
        return postService.findPostPublicationsByProjectAuthorId(projectId);
    }

    @GetMapping("hashtag/{hashtag}")
    public List<PostDto> getAllByHashtag(@PathVariable String hashtag,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postService.findAllByHashtag(hashtag, pageable);
    }
}
