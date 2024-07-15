package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.LikePostEvent;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.LikePostProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like.LikeValidatorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;
    private final LikeValidatorImpl likeValidator;
    private final LikePostProducer likePostProducer;

    @Override
    @Transactional
    public LikeDto addLikeOnPost(long userId, long postId) {
        LikeDto likeDto = createLikeDto(null, userId, dto -> dto.setPostId(postId));

        likeValidator.validateUserExistence(userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post with postId:" + postId + " not found"));

        likeValidator.validateAndGetPostToLike(userId, post);

        Like like = likeMapper.toEntity(likeDto);
        post.getLikes().add(like);
        like = likeRepository.save(like);

        likePostProducer.publish(new LikePostEvent(postId, post.getAuthorId(), userId, LocalDateTime.now()));

        log.info("Like with likeId = {} was added on post with postId = {} by user with userId = {}", like.getId(), postId, userId);

        return likeMapper.toDto(like);
    }

    @Override
    @Transactional
    public void removeLikeFromPost(long likeId, long userId, long postId) {
        LikeDto likeDto = createLikeDto(likeId, userId, dto -> dto.setPostId(postId));

        Like like = likeMapper.toEntity(likeDto);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post with postId:" + likeDto.getPostId() + " not found"));
        post.getLikes().remove(like);

        log.info("Like with likeId = {} was removed from post with postId = {} by user with userId = {}", like.getId(), postId, userId);

        likeRepository.delete(like);
    }

    @Override
    @Transactional
    public LikeDto addLikeOnComment(long userId, long commentId) {
        LikeDto likeDto = createLikeDto(null, userId, dto -> dto.setCommentId(commentId));

        likeValidator.validateUserExistence(userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment with commentId:" + likeDto.getCommentId() + " not found"));

        likeValidator.validateCommentToLike(userId, comment);

        Like like = likeMapper.toEntity(likeDto);
        comment.getLikes().add(like);
        like = likeRepository.save(like);

        log.info("Like with likeId = {} was added on comment with commentId = {} by user with userId = {}", like.getId(), commentId, userId);

        return likeMapper.toDto(like);
    }

    @Override
    @Transactional
    public void removeLikeFromComment(long likeId, long userId, long commentId) {
        LikeDto likeDto = createLikeDto(likeId, userId, dto -> dto.setCommentId(commentId));

        Like like = likeMapper.toEntity(likeDto);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment with commentId:" + likeDto.getCommentId() + " not found"));
        comment.getLikes().remove(like);

        log.info("Like with likeId = {} was removed from comment with commentId = {} by user with userId = {}", like.getId(), commentId, userId);

        likeRepository.delete(like);
    }

    private LikeDto createLikeDto(Long id, Long userId, Consumer<LikeDto> function) {
        LikeDto likeDto = new LikeDto();
        likeDto.setId(id);
        likeDto.setUserId(userId);
        function.accept(likeDto);
        return likeDto;
    }
}
