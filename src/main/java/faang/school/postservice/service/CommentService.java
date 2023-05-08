package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final PostService postService;

    @Transactional(readOnly = true)
    public Comment findById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id " + id + " not found"));
    }

    @Transactional
    public CommentDto createComment(long postId, CommentDto commentDto) {
        commentValidator.validateCreation(commentDto);
        Post post = postService.findById(postId);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(long commentId, CommentDto commentDto) {
        Comment comment = findById(commentId);
        commentValidator.validateUpdate(comment, commentDto);
        commentMapper.update(comment, commentDto);
        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getComments(long postId) {
        return commentRepository.findAllByPostId(postId)
                .stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }
}
