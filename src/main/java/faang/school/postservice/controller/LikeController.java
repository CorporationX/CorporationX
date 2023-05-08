package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}/like")
    public LikeDto likePost(@PathVariable long postId, @RequestBody @Valid LikeDto like) {
        return likeService.likePost(postId, like);
    }

    @PostMapping("/comment/{commentId}/like")
    public LikeDto likeComment(@PathVariable long commentId, @RequestBody @Valid LikeDto like) {
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

    @GetMapping("/post/{postId}/like/users")
    public List<UserDto> getUsersLikedPosts(@PathVariable long postId) {
        return likeService.getUsersLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}/like/users")
    public List<UserDto> getUsersLikedComments(@PathVariable long commentId) {
        return likeService.getUsersLikedComment(commentId);
    }
}
