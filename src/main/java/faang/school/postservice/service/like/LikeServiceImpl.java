package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final LikeValidator validator;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper mapper;

    @Override
    public LikeDto addLikeOnPost(LikeDto likeDto) {
        validator.validate(likeDto);

        Like like = mapper.toEntity(likeDto);

        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("post with postId:" + likeDto.getPostId() + " not found"));
        post.getLikes().add(like);

        Like saved = likeRepository.save(like);

        return mapper.toDto(saved);
    }

    @Override
    public void removeLikeFromPost(LikeDto likeDto) {
        validator.validate(likeDto);

        Like like = mapper.toEntity(likeDto);

        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("post with postId:" + likeDto.getPostId() + " not found"));
        post.getLikes().remove(like);

        likeRepository.delete(like);
    }

    @Override
    public LikeDto addLikeOnComment(LikeDto likeDto) {
        validator.validate(likeDto);

        Like like = mapper.toEntity(likeDto);

        Comment comment = commentRepository.findById(likeDto.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException("comment with commentId:" + likeDto.getCommentId() + " not found"));
        comment.getLikes().add(like);

        Like saved = likeRepository.save(like);

        return mapper.toDto(saved);
    }

    @Override
    public void removeLikeFromComment(LikeDto likeDto) {
        validator.validate(likeDto);

        Like like = mapper.toEntity(likeDto);

        Comment comment = commentRepository.findById(likeDto.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException("comment with commentId:" + likeDto.getCommentId() + " not found"));
        comment.getLikes().remove(like);

        likeRepository.delete(like);
    }
}
