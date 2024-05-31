package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostMapper postMapper;

    public Set<String> getHashtags(String content) {

        String[] words = content.split(" ");

        return Arrays.stream(words)
                .filter(word -> word.startsWith("#") && word.length() > 1)
                .map(word -> word.substring(1))
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostsByHashtag(String hashtag) {

        return hashtagRepository.findAllByHashtag(hashtag).stream()
                .map(Hashtag::getPost)
                .map(postMapper::toDto)
                .toList();
    }

    @Transactional
    public void addHashtags(Post post) {
        Set<String> hashtags = getHashtags(post.getContent());
        hashtags.forEach(hashtag -> addHashtag(hashtag, post));
    }

    @Transactional
    public void deleteHashtags(Post post) {
        Set<String> hashtags = getHashtags(post.getContent());
        hashtags.forEach(hashtag -> deleteHashtag(hashtag, post));
    }

    @Transactional
    public void updateHashtags(Post post) {
        Set<String> hashtags = getHashtags(post.getContent());
        List<Hashtag> entities = hashtagRepository.findAllByPostId(post.getId());

        entities.forEach(entity -> {
            if (!hashtags.contains(entity.getHashtag())) {
                deleteHashtag(entity.getHashtag(), post);
            } else {
                hashtags.remove(entity.getHashtag());
            }
        });

        hashtags.forEach(hashtag -> addHashtag(hashtag, post));
    }

    public void addHashtag(String hashtag, Post post) {
        Hashtag entity = Hashtag.builder()
                .hashtag(hashtag)
                .post(post)
                .build();

        try {
            hashtagRepository.save(entity);
        } catch (DataIntegrityViolationException | JpaSystemException ignored) {}
    }

    public void deleteHashtag(String hashtag, Post post) {
        hashtagRepository.deleteByHashtagAndPostId(hashtag, post.getId());
    }
}