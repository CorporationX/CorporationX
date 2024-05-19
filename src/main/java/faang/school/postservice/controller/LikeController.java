package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final UserContext userContext;

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto likePost(@Positive @PathVariable long postId) {
        long userId = userContext.getUserId();
        return likeService.addLikeOnPost(userId, postId);
    }

    @DeleteMapping("/post/{postId}/{likeId}")
    public void deleteLikeFromPost(@Positive @PathVariable long postId,
                                   @Positive @PathVariable long likeId) {
        long userId = userContext.getUserId();
        likeService.removeLikeFromPost(likeId, userId, postId);
    }

    @PostMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto likeComment(@Positive @PathVariable long commentId) {
        long userId = userContext.getUserId();
        return likeService.addLikeOnComment(userId, commentId);
    }

    @DeleteMapping("/comment/{commentId}/{likeId}")
    public void deleteLikeFromComment(@Positive @PathVariable long commentId,
                                      @Positive @PathVariable long likeId) {
        long userId = userContext.getUserId();
        likeService.removeLikeFromComment(likeId, userId, commentId);
    }
}
