package faang.school.postservice.scheduler;

import faang.school.postservice.entity.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    @Value("${post.moderator.max-list-size}")
    private Integer maxListSize;

    private final PostRepository postRepository;
    private final PostService postService;

    @Scheduled(cron = "${post.moderator.scheduler.cron}")
    public void moderatePosts() {
        List<Post> posts = postRepository.findAllUncheckedPosts();

        if (posts.isEmpty()) {
            log.info("There are no posts for moderation");
            return;
        }

        List<List<Post>> partitionedPostToVerify = ListUtils.partition(posts, maxListSize);
        partitionedPostToVerify.forEach(postService::verifyPost);
    }
}
