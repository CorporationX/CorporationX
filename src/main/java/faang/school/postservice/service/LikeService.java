package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private static final int USER_BATCH_SIZE = 100;

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeMapper likeMapper;
    private final LikeValidator likeValidator;
    private final UserServiceClient userServiceClient;

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

    @Transactional(readOnly = true)
    public List<UserDto> getUsersLikedPost(long postId) {
        List<Long> userIds = sortAndGetUserIds(likeRepository.findByPostId(postId));
        return fetchUsers(userIds);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersLikedComment(long commentId) {
        List<Long> userIds = sortAndGetUserIds(likeRepository.findByCommentId(commentId));
        return fetchUsers(userIds);
    }

    private List<UserDto> fetchUsers(List<Long> userIds) {
        List<UserDto> allUsersLikedPost = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += USER_BATCH_SIZE) {
            int toIndex = Math.min(i + USER_BATCH_SIZE, userIds.size());
            List<UserDto> users = userServiceClient.getUsersByIds(userIds.subList(i, toIndex));
            allUsersLikedPost.addAll(users);
        }
        return allUsersLikedPost;
    }

    private List<Long> sortAndGetUserIds(List<Like> likes) {
        return likes.stream()
                .sorted((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()))
                .map(Like::getUserId)
                .toList();
    }
}
