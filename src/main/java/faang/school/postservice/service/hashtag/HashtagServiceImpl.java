package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostMapper postMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPostsByHashtag(String hashtag) {

        return hashtagRepository.findAllByHashtag(hashtag).stream()
                .map(Hashtag::getPost)
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hashtag> getHashtagsByPostId(long postId) {
        return hashtagRepository.findAllByPostId(postId);
    }

    @Override
    @Transactional
    public void addHashtag(String hashtag, Post post) {
        Hashtag entity = Hashtag.builder()
                .hashtag(hashtag)
                .post(post)
                .build();

        hashtagRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteHashtag(String hashtag, Post post) {
        hashtagRepository.deleteByHashtagAndPostId(hashtag, post.getId());
    }
}