package faang.school.postservice.service.redis.feed;

import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.event.post.DeletePostEvent;
import faang.school.postservice.event.post.NewPostEvent;
import faang.school.postservice.event.post.PostViewEvent;

import java.util.SortedSet;

public interface FeedCacheService {

    SortedSet<PostDto> getNewsFeed(Long userId);

    void addPostInFeed(NewPostEvent event);

    void deletePostFromFeed(DeletePostEvent event);

    void addPostView(PostViewEvent event);
}
