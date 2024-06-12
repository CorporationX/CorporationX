package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.util.List;

public interface HashtagService {

    List<PostDto> getPostsByHashtag(String hashtag);

    List<HashtagDto> getHashtagsByPostId(long postId);

    void addHashtag(String hashtag, Post post);

    void deleteHashtag(String hashtag, Post post);
}
