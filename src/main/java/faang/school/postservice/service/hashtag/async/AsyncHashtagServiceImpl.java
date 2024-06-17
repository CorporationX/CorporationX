package faang.school.postservice.service.hashtag.async;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.hashtag.HashtagService;
import faang.school.postservice.util.HashtagSpliterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncHashtagServiceImpl implements AsyncHashtagService {

    private final HashtagService hashtagService;

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<PostDto>> getPostsByHashtag(String hashtag) {
        return CompletableFuture.completedFuture(hashtagService.getPostsByHashtag(hashtag));
    }

    @Override
    @Async("taskExecutor")
    public void addHashtags(Post post) {
        Set<String> hashtags = HashtagSpliterator.getHashtags(post.getContent());
        hashtags.forEach(hashtag -> hashtagService.addHashtag(hashtag, post));
    }

    @Override
    @Async("taskExecutor")
    public void removeHashtags(Post post) {
        Set<String> hashtags = HashtagSpliterator.getHashtags(post.getContent());
        hashtags.forEach(hashtag -> hashtagService.deleteHashtag(hashtag, post));
    }

    @Override
    @Async("taskExecutor")
    public void updateHashtags(Post post) {
        Set<String> hashtags = HashtagSpliterator.getHashtags(post.getContent());
        hashtags.forEach(hashtag -> hashtagService.updateHashtag(hashtag, post));
    }
}