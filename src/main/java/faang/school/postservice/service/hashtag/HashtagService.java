package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.util.List;
import java.util.Set;

public interface HashtagService {

    Set<String> getHashtags(String content);

    List<PostDto> getPostsByHashtag(String hashtag);

    void addHashtags(Post post);

    void deleteHashtags(Post post);

    void updateHashtags(Post post);

    void addHashtag(String hashtag, Post post);

    void deleteHashtag(String hashtag, Post post);
}
