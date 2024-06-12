package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncHashtagService {

    void addHashtags(Post post);

    void removeHashtags(Post post);

    void updateHashtags(Post post);

    CompletableFuture<List<PostDto>> getPostsByHashtag(String hashtag);
}
