package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto likePost(@RequestBody LikeDto likeDto, @PathVariable long postId) {
        likeDto.setPostId(postId);
        return likeService.addLikeOnPost(likeDto);
    }

    @DeleteMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeFromPost(@RequestBody LikeDto likeDto, @PathVariable long postId) {
        likeDto.setPostId(postId);
        likeService.removeLikeFromPost(likeDto);
    }

    @PostMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto likeComment(@RequestBody LikeDto likeDto, @PathVariable long commentId) {
        likeDto.setCommentId(commentId);
        return likeService.addLikeOnComment(likeDto);
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeFromComment(@RequestBody LikeDto likeDto, @PathVariable long commentId) {
        likeDto.setCommentId(commentId);
        likeService.removeLikeFromComment(likeDto);
    }
}
