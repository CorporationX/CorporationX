package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}/like")
    public LikeDto likePost(@PathVariable long postId, @RequestBody LikeDto like) {
        return likeService.likePost(postId, like);
    }

    @PostMapping("/comment/{commentId}/like")
    public LikeDto likeComment(@PathVariable long commentId, @RequestBody LikeDto like) {
        return likeService.likeComment(commentId, like);
    }

    @DeleteMapping("/user/{userId}/post/{postId}/like")
    public void unlikePost(@PathVariable long userId, @PathVariable long postId) {
        likeService.unlikePost(userId, postId);
    }

    @DeleteMapping("/user/{userId}/comment/{commentId}/like")
    public void unlikeComment(@PathVariable long userId, @PathVariable long commentId) {
        likeService.unlikeComment(userId, commentId);
    }
}
