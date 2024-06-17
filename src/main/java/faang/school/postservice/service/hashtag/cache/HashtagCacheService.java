package faang.school.postservice.service.hashtag.cache;

import faang.school.postservice.dto.post.PostHashtagDto;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface HashtagCacheService {

    Set<PostHashtagDto> getPostsByHashtag(String hashtag, Pageable pageable);

    void addPostToHashtag(String hashtag, PostHashtagDto post);

    void removePostFromHashtag(String hashtag, PostHashtagDto post);
}
