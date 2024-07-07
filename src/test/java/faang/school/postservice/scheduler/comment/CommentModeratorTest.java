package faang.school.postservice.scheduler.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentModeratorTest {

    @InjectMocks
    private CommentModerator commentModerator;

    @Mock
    private ModerationDictionary moderationDictionary;

    @Mock
    private ExecutorService commentModeratorExecutorService;

    @Mock
    private CommentRepository commentRepository;

    @Test
    void moderateBatch() {
        Comment comment1 = new Comment();
        comment1.setContent("This is a test comment");
        comment1.setCreatedAt(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setContent("This is another test comment");
        comment2.setCreatedAt(LocalDateTime.now());

        List<Comment> batch = Arrays.asList(comment1, comment2);
        when(moderationDictionary.containsBadWord(anyString())).thenReturn(false);

        commentModerator.moderateBatch(batch);

        verify(commentRepository, times(2)).save(any(Comment.class));
    }
}