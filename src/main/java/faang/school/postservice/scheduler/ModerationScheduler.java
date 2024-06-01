package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationScheduler {

    @Value("${post.max-list-size}")
    private int maxListSize;
    private final PostRepository postRepository;
    private final PostService postService;

    @Scheduled(cron = "${post.moderator.scheduler.cron}")
    public void moderatePosts() {
        List<Post> posts = postRepository.findAll();

        if (posts.isEmpty()) {
            log.info("posts were empty");
            return;
        }

        List<List<Post>> partitionedPostToVerify = ListUtils.partition(posts, maxListSize);
        partitionedPostToVerify.forEach(postService::verifyPost);
    }
}
