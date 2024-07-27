package faang.school.postservice.service.like;

import faang.school.postservice.entity.dto.like.LikeDto;
import faang.school.postservice.event.like.LikePostEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.entity.model.Comment;
import faang.school.postservice.entity.model.Like;
import faang.school.postservice.entity.model.Post;
import faang.school.postservice.kafka.producer.LikePostProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like.LikeValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeValidatorImpl likeValidator;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeMapper mapper;
    @Mock
    private LikePostProducer likePostProducer;
    @InjectMocks
    private LikeServiceImpl likeService;
    @Captor
    private ArgumentCaptor<LikePostEvent> captorForLikeEvent;

    private final long userId = 4L;
    private final long postId = 2L;
    private final long commentId = 3L;
    private final long likeId = 1L;
    private LikeDto dto;
    private Like like;
    private Post post;
    private Comment comment;

    @BeforeEach
    void init() {
        dto = LikeDto.builder()
                .id(likeId)
                .userId(userId)
                .postId(postId)
                .build();

        like = Like.builder()
                .id(likeId)
                .build();

        post = Post.builder()
                .id(2L)
                .likes(new ArrayList<>())
                .build();

        comment = Comment.builder()
                .id(3L)
                .likes(new ArrayList<>())
                .build();
    }

    @Test
    void removeLikeFromPost() {
        post.getLikes().add(like);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findById(likeId)).thenReturn(Optional.of(like));

        likeService.removeLikeFromPost(likeId, userId, postId);
        assertTrue(post.getLikes().isEmpty());

        InOrder inOrder = inOrder(likeValidator, mapper, likeRepository, postRepository);
        inOrder.verify(postRepository, times(1)).findById(postId);
        inOrder.verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void removeLikeFromComment() {
        comment.getLikes().add(like);

        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        when(likeRepository.findById(likeId)).thenReturn(Optional.ofNullable(like));

        likeService.removeLikeFromComment(likeId, userId, commentId);
        assertTrue(comment.getLikes().isEmpty());

        InOrder inOrder = inOrder(likeValidator, mapper, likeRepository, commentRepository);
        inOrder.verify(commentRepository, times(1)).findById(commentId);
        inOrder.verify(likeRepository, times(1)).delete(like);
    }
}