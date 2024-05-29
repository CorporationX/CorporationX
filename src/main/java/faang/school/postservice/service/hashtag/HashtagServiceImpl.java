package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;
    private final HashtagMapper hashtagMapper;
    private final PostMapper postMapper;

    @Override
    public String[] getHashtags(String content) {
        String[] words = content.split(" ");
        return Arrays.stream(words).filter(word -> word.startsWith("#")).toArray(String[]::new);
    }

    @Override
    public List<PostDto> getPostsByHashtag(String hashtag) {
        return hashtagRepository.findByHashtag(hashtag).stream()
                .map(Hashtag::getPost)
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<HashtagDto> addHashTag(Post post) {

        String[] hashtags = getHashtags(post.getContent());
        List<HashtagDto> hashtagDtos = new ArrayList<>();

        for (String hashtag : hashtags) {

            Hashtag entity = Hashtag.builder()
                    .hashtag(hashtag)
                    .post(post)
                    .build();

            entity = hashtagRepository.save(entity);

            hashtagDtos.add(hashtagMapper.toDto(entity));
        }

        return hashtagDtos;
    }
}
