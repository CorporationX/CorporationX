package faang.school.postservice.service.like;

import faang.school.postservice.entity.dto.like.LikeDto;
import faang.school.postservice.entity.model.Comment;
import faang.school.postservice.entity.model.Like;
import faang.school.postservice.entity.model.Post;
import faang.school.postservice.event.like.LikePostEvent;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.kafka.producer.LikePostProducer;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like.LikeValidatorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        likeValidator.validateUserExistence(userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with postId:" + postId + " not found"));

        likeValidator.validateAndGetPostToLike(userId, post);

        Like like = Like.builder()
                .userId(userId)
                .post(post)
                .build();

        like = likeRepository.save(like);
        post.getLikes().add(like);

        LikeDto dto = likeMapper.toDto(like);
        likePostProducer.publish(new LikePostEvent(dto.getPostId()));

        log.info("Like with likeId = {} was added on post with postId = {} by user with userId = {}", like.getId(), postId, userId);

        return dto;
    }

    @Override
    @Transactional
    public void removeLikeFromPost(long likeId, long userId, long postId) {
        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new NotFoundException("Like with likeId:" + likeId + " not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with postId:" + postId + " not found"));

        post.getLikes().remove(like);
        likeRepository.delete(like);

        log.info("Like with likeId = {} was removed from post with postId = {} by user with userId = {}", likeId, postId, userId);
    }

    @Override
    @Transactional
    public LikeDto addLikeOnComment(long userId, long commentId) {
        likeValidator.validateUserExistence(userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with commentId:" + commentId + " not found"));

        likeValidator.validateCommentToLike(userId, comment);

        Like like = Like.builder()
                .userId(userId)
                .comment(comment)
                .build();

        like = likeRepository.save(like);
        comment.getLikes().add(like);

        LikeDto dto = likeMapper.toDto(like);

        log.info("Like with likeId = {} was added on comment with commentId = {} by user with userId = {}", like.getId(), commentId, userId);

        return dto;
    }

    @Override
    @Transactional
    public void removeLikeFromComment(long likeId, long userId, long commentId) {
        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new NotFoundException("Like with likeId:" + likeId + " not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with commentId:" + commentId + " not found"));

        comment.getLikes().remove(like);
        likeRepository.delete(like);

        log.info("Like with likeId = {} was removed from comment with commentId = {} by user with userId = {}", likeId, commentId, userId);
    }

    @Override
    public List<LikeDto> getAllPostLikes(long postId) {
        return likeRepository.findByPostId(postId).stream()
                .map(likeMapper::toDto)
                .toList();
    }

    @Override
    public List<LikeDto> getAllCommentLikes(long commentId) {
        return likeRepository.findByCommentId(commentId).stream()
                .map(likeMapper::toDto)
                .toList();
    }
}