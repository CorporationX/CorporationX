package faang.school.postservice.service.hashtag.async;

import faang.school.postservice.dto.post.PostHashtagDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncHashtagService {

    CompletableFuture<List<PostHashtagDto>> getPostsByHashtag(String hashtag, Pageable pageable);

    void addHashtags(PostHashtagDto post);

    void removeHashtags(PostHashtagDto post);

    void updateHashtags(PostHashtagDto post);

    void updateScore(PostHashtagDto post);
}
