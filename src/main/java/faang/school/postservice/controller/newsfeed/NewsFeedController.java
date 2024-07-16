package faang.school.postservice.controller.newsfeed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.service.redis.feed.FeedCacheService;
import faang.school.postservice.service.redis.heater.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.SortedSet;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class NewsFeedController {

    private final UserContext userContext;
    private final FeedCacheService feedService;
    private final FeedHeaterService feedHeaterService;

    @GetMapping
    public SortedSet<PostDto> getNewsFeed() {
        long userId = userContext.getUserId();
        return feedService.getNewsFeed(userId);
    }

    @PostMapping("/heat")
    public void heatFeed() {
        feedHeaterService.feedHeat();
    }
}
