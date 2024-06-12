package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        Set<String> hashtags = getHashtags(post.getContent());
        hashtags.forEach(hashtag -> hashtagService.addHashtag(hashtag, post));
    }

    @Override
    @Async("taskExecutor")
    public void removeHashtags(Post post) {
        Set<String> hashtags = getHashtags(post.getContent());
        hashtags.forEach(hashtag -> hashtagService.deleteHashtag(hashtag, post));
    }

    @Override
    @Async("taskExecutor")
    public void updateHashtags(Post post) {
        Set<String> hashtags = getHashtags(post.getContent());
        List<HashtagDto> entities = hashtagService.getHashtagsByPostId(post.getId());

        entities.forEach(entity -> {
            if (!hashtags.contains(entity.getHashtag())) {
                hashtagService.deleteHashtag(entity.getHashtag(), post);
            } else {
                hashtags.remove(entity.getHashtag());
            }
        });

        hashtags.forEach(hashtag -> hashtagService.addHashtag(hashtag, post));
    }

    private Set<String> getHashtags(String content) {

        String[] words = content.split(" ");

        return Arrays.stream(words)
                .filter(word -> word.startsWith("#") && word.length() > 1)
                .map(word -> word.substring(1))
                .collect(Collectors.toSet());
    }
}