package faang.school.postservice.controller.newsfeed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.service.redis.feed.FeedCacheService;
import faang.school.postservice.service.redis.heater.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class NewsFeedController {

    private final UserContext userContext;
    private final FeedCacheService feedService;
    private final FeedHeaterService feedHeaterService;

    @GetMapping
    public LinkedHashSet<RedisPost> getNewsFeed(@RequestParam(value = "lastPostId", required = false) Long lastPostId) {
        long userId = userContext.getUserId();
        return feedService.getNewsFeed(userId, lastPostId);
    }

    @PostMapping("/heat")
    public void heatFeed() {
        feedHeaterService.feedHeat();
    }
}

