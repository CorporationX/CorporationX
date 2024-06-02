package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostVerifiedStatus;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ModerationSchedulerTest {

    @Mock
    PostRepository postRepository;
    @InjectMocks
    ModerationScheduler moderationScheduler;
    @Mock
    PostService postService;
    Post post;

    @BeforeEach
    void init() {
        post = new Post();
        post.setId(1);
        post.setContent("Hello world");
        post.setProjectId(1L);
        post.setIsVerify(PostVerifiedStatus.UNCHECKED);

        int maxListSize = 1;
        ReflectionTestUtils.setField(moderationScheduler, "maxListSize", maxListSize);
    }


    @Test
    public void testModeratePosts() {
        List<Post> posts = List.of(post);
        Mockito.when(postRepository.findAllUncheckedPosts()).thenReturn(posts);
        Mockito.doNothing().when(postService).verifyPost(posts);

        moderationScheduler.moderatePosts();

        Mockito.verify(postRepository, Mockito.times(1)).findAllUncheckedPosts();
        Mockito.verify(postService, Mockito.times(1)).verifyPost(posts);
    }
}
