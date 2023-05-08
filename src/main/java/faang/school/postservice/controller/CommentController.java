package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}/comment")
    public CommentDto createComment(@PathVariable long postId, @RequestBody @Valid CommentDto comment) {
        return commentService.createComment(postId, comment);
    }

    @PutMapping("/comment/{commentId}")
    public CommentDto updateComment(@PathVariable long commentId,
                                    @RequestBody @Valid CommentDto comment) {
        return commentService.updateComment(commentId, comment);
    }

    @GetMapping("/post/{postId}/comment")
    public List<CommentDto> getComments(@PathVariable long postId) {
        return commentService.getComments(postId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
    }
}
