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
import faang.school.postservice.validator.like.LikeValidatorImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper mapper;
    private final LikeValidatorImpl likeValidator;

    @Override
    @Transactional
    public LikeDto addLikeOnPost(long userId, long postId) {
        LikeDto likeDto = LikeDto.builder()
                .userId(userId)
                .postId(postId)
                .build();

        likeValidator.validateLike(likeDto);

        Like like = mapper.toEntity(likeDto);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("post with postId:" + postId + " not found"));
        post.getLikes().add(like);

        Like saved = likeRepository.save(like);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void removeLikeFromPost(long likeId, long userId, long postId) {
        LikeDto likeDto = LikeDto.builder()
                .id(likeId)
                .userId(userId)
                .postId(postId)
                .build();

        likeValidator.validateLike(likeDto);

        Like like = mapper.toEntity(likeDto);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("post with postId:" + likeDto.getPostId() + " not found"));
        post.getLikes().remove(like);

        likeRepository.delete(like);
    }

    @Override
    @Transactional
    public LikeDto addLikeOnComment(long userId, long commentId) {
        LikeDto likeDto = LikeDto.builder()
                .userId(userId)
                .commentId(commentId)
                .build();

        likeValidator.validateLike(likeDto);

        Like like = mapper.toEntity(likeDto);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("comment with commentId:" + likeDto.getCommentId() + " not found"));
        comment.getLikes().add(like);

        Like saved = likeRepository.save(like);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void removeLikeFromComment(long likeId, long userId, long commentId) {
        LikeDto likeDto = LikeDto.builder()
                .id(likeId)
                .userId(userId)
                .commentId(commentId)
                .build();

        likeValidator.validateLike(likeDto);

        Like like = mapper.toEntity(likeDto);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("comment with commentId:" + likeDto.getCommentId() + " not found"));
        comment.getLikes().remove(like);

        likeRepository.delete(like);
    }
}
