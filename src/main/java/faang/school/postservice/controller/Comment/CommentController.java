package faang.school.postservice.controller.Comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.comment.CommentToCreateDto;
import faang.school.postservice.entity.dto.comment.CommentToUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserContext userContext;

    @Operation(summary = "Create a new comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.class))})
    })
    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable("postId") long postId,
                                    @RequestBody @Valid CommentToCreateDto commentDto) {
        long userId = userContext.getUserId();
        return commentService.createComment(postId, userId, commentDto);
    }

    @Operation(summary = "Get all comments for a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of post comments",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.class))})
    })
    @GetMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllPostComments(@PathVariable("postId") long postId) {
        return commentService.getAllPostComments(postId);
    }

    @Operation(summary = "Update a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.class))})
    })
    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable("commentId") long commentId,
                                    @RequestBody @Valid CommentToUpdateDto commentDto) {
        long userId = userContext.getUserId();
        return commentService.updateComment(commentId, userId, commentDto);
    }

    @Operation(summary = "Delete a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentDto.class))})
    })
    @DeleteMapping("/{commentId}/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto deleteComment(@PathVariable("commentId") long commentId,
                                    @PathVariable("postId") long postId) {
        long userId = userContext.getUserId();
        return commentService.deleteComment(postId, commentId, userId);
    }
}
