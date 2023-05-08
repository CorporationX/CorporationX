package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeMapper likeMapper;
    private final LikeValidator likeValidator;

    @Transactional
    public LikeDto likePost(long postId, LikeDto like) {
        likeValidator.validate(like);
        Post post = postService.findById(postId);
        Like entity = likeMapper.toEntity(like);
        entity.setPost(post);
        like = likeMapper.toDto(likeRepository.save(entity));
        like.setPostId(postId);
        return like;
    }

    @Transactional
    public LikeDto likeComment(long commentId, LikeDto like) {
        likeValidator.validate(like);
        Comment comment = commentService.findById(commentId);
        Like entity = likeMapper.toEntity(like);
        entity.setComment(comment);
        like = likeMapper.toDto(likeRepository.save(entity));
        like.setCommentId(commentId);
        return like;
    }

    @Transactional
    public void unlikePost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public void unlikeComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}
