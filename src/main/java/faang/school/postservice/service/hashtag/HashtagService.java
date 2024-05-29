package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.util.List;

public interface HashtagService {

    String[] getHashtags(String content);

    List<PostDto> getPostsByHashtag(String hashtag);

    List<HashtagDto> addHashTag(Post post);
}
