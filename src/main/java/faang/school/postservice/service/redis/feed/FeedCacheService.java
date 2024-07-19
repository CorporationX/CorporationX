package faang.school.postservice.service.redis.feed;

import faang.school.postservice.entity.model.redis.RedisPost;
import faang.school.postservice.event.post.DeletePostEvent;
import faang.school.postservice.event.post.NewPostEvent;
import faang.school.postservice.event.post.PostViewEvent;

import java.util.LinkedHashSet;

public interface FeedCacheService {

    LinkedHashSet<RedisPost> getNewsFeed(Long userId, Long lastPostId);

    void addPostInFeed(NewPostEvent event);

    void deletePostFromFeed(DeletePostEvent event);

    void addPostView(PostViewEvent event);
}
