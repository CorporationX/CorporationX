package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.hashtag.cache.HashtagCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostMapper postMapper;
    private final HashtagCacheService hashtagCacheService;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPostsByHashtag(String hashtag) {

        return hashtagCacheService.getPostsByHashtag(hashtag).stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPage(int offset) {

        Pageable pageable = PageRequest.of(offset, 20);
        Page<Post> dataPage = postRepository.findAll(pageable);

        return dataPage.stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void addHashtag(String hashtag, Post post) {

        hashtagRepository.save(build(hashtag, post));

        log.info("Hashtag #{} added for post with postId={}", hashtag, post.getId());

        hashtagCacheService.addPostToHashtag(hashtag, post);
    }

    @Override
    @Transactional
    public void deleteHashtag(String hashtag, Post post) {

        hashtagRepository.deleteByHashtagAndPostId(hashtag, post.getId());

        log.info("Post with postId={} deleted from hashtag #{}", post.getId(), hashtag);

        hashtagCacheService.removePostFromHashtag(hashtag, post);
    }

    @Override
    @Transactional
    public void updateHashtag(String hashtag, Post post) {

        if (hashtagRepository.existsByHashtag(hashtag)) {
            deleteHashtag(hashtag, post);
        } else {
            addHashtag(hashtag, post);
        }
    }

    private Hashtag build(String hashtag, Post post) {
        return Hashtag.builder()
                .hashtag(hashtag)
                .post(post)
                .build();
    }
}