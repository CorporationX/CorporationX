package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.comment.CommentToCreateDto;
import faang.school.postservice.entity.dto.comment.CommentToUpdateDto;
import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.entity.model.redis.RedisComment;
import faang.school.postservice.entity.model.redis.RedisUser;
import faang.school.postservice.event.comment.NewCommentEvent;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.entity.model.Comment;
import faang.school.postservice.entity.model.Post;
import faang.school.postservice.kafka.producer.NewCommentProducer;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisCommentRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.commonMethods.CommonServiceMethods;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.redis.CachedEntityBuilder;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommonServiceMethods commonServiceMethods;
    private final NewCommentProducer newCommentPublisher;
    private final UserServiceClient userServiceClient;
    private final RedisCommentRepository redisCommentRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisCommentMapper redisCommentMapper;
    private final RedisUserMapper redisUserMapper;

    @Value("${spring.data.redis.ttl.comment}")
    private Long commentTtl;

    @Value("${spring.data.redis.ttl.user}")
    private Long userTtl;

    @Override
    public CommentDto createComment(long postId, long userId, CommentToCreateDto commentDto) {

        Post post = commonServiceMethods.findEntityById(postRepository, postId, "Post");
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userId);
        comment.setPost(post);

        commentValidator.validateCreateComment(userId);

        comment = commentRepository.save(comment);
        CommentDto dto = commentMapper.toDto(comment);

        saveUserToRedis(dto.getAuthorId());
        saveNewCommentToRedis(dto);
        newCommentPublisher.publish(new NewCommentEvent(dto));

        log.info("Created comment on post {} authored by {}", postId, userId);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllPostComments(long postId) {

        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updateComment(long commentId, long userId, CommentToUpdateDto updatedCommentDto) {

        Comment commentToUpdate = commonServiceMethods.findEntityById(commentRepository, commentId, "Comment");

        commentValidator.validateUpdateAlbum(commentToUpdate, userId);

        commentMapper.update(updatedCommentDto, commentToUpdate);
        log.info("Updated comment {} on post {} authored by {}", commentId, commentToUpdate.getPost().getId(), userId);
        commentRepository.save(commentToUpdate);
        return commentMapper.toDto(commentToUpdate);
    }

    @Override
    public CommentDto deleteComment(long postId, long commentId, long userId) {

        Comment comment = commonServiceMethods.findEntityById(commentRepository, commentId, "Comment");
        CommentDto commentToDelete = commentMapper.toDto(comment);

        commentValidator.validateDeleteAlbum(postId, userId, comment);

        commentRepository.deleteById(commentId);
        log.info("Deleted comment {} on post {} authored by {}", commentId, comment.getPost().getId(), userId);
        return commentToDelete;
    }

    @Override
    public CommentDto getById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id %d not found", commentId)));
        return commentMapper.toDto(comment);
    }

    public void saveNewCommentToRedis(CommentDto dto) {
        RedisComment newComment = redisCommentMapper.toRedisDto(dto);
        newComment.setVersion(1L);
        newComment.setTtl(commentTtl);
        redisCommentRepository.save(newComment.getId(), newComment);
    }

    public void saveUserToRedis(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);

        HashSet<Long> followingsIds = userServiceClient.getFollowings(userId).stream()
                .map(UserDto::getId).collect(Collectors.toCollection(HashSet::new));

        HashSet<Long> followersIds = userServiceClient.getFollowers(userId).stream()
                .map(UserDto::getId).collect(Collectors.toCollection(HashSet::new));

        RedisUser redisUser = redisUserMapper.toRedisDto(userDto);

        redisUser.setFollowingsIds(followingsIds);
        redisUser.setFollowersIds(followersIds);
        redisUser.setVersion(1L);
        redisUser.setTtl(userTtl);

        redisUserRepository.save(redisUser.getId(), redisUser);
    }
}