package faang.school.postservice.service.hashtag.async;

import faang.school.postservice.entity.dto.post.PostHashtagDto;
import faang.school.postservice.service.hashtag.HashtagService;
import faang.school.postservice.util.HashtagSpliterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
    public CompletableFuture<List<PostHashtagDto>> getPostsByHashtag(String hashtag, Pageable pageable) {
        return CompletableFuture.completedFuture(hashtagService.getPageOfPostsByHashtag(hashtag, pageable));
    }

    @Override
    @Async("taskExecutor")
    public void addHashtags(PostHashtagDto post) {
        Set<String> hashtags = HashtagSpliterator.getHashtags(post.getContent());
        hashtags.forEach(hashtag -> hashtagService.addHashtag(hashtag, post));
    }

    @Override
    @Async("taskExecutor")
    public void removeHashtags(PostHashtagDto post) {
        Set<String> hashtags = HashtagSpliterator.getHashtags(post.getContent());
        hashtags.forEach(hashtag -> hashtagService.deleteHashtag(hashtag, post));
    }

    @Override
    @Async("taskExecutor")
    public void updateHashtags(PostHashtagDto post) {
        Set<String> hashtags = HashtagSpliterator.getHashtags(post.getContent());
        hashtags.forEach(hashtag -> hashtagService.updateHashtag(hashtag, post));
    }

    @Override
    @Async("taskExecutor")
    public void updateScore(PostHashtagDto post) {
        Set<String> hashtags = HashtagSpliterator.getHashtags(post.getContent());
        hashtags.forEach(hashtag -> hashtagService.updateScore(hashtag, post));
    }
}