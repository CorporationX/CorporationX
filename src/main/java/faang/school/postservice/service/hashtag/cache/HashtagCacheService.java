package faang.school.postservice.service.hashtag.cache;

import faang.school.postservice.model.Post;

import java.util.Set;

public interface HashtagCacheService {

    Set<Post> getPostsByHashtag(String hashtag);

    void addPostToHashtag(String hashtag, Post post);

    void removePostFromHashtag(String hashtag, Post post);
}
