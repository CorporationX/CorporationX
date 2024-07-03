package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    Post findById(Long id);

    PostDto create(PostCreateDto postCreateDto);

    PostDto publish(Long id);

    PostDto update(Long id, PostUpdateDto postUpdateDto);

    void deleteById(Long id);

    List<PostDto> findAllByHashtag(String hashtag, Pageable pageable);

    List<PostDto> findPostDraftsByUserAuthorId(Long id);

    List<PostDto> findPostDraftsByProjectAuthorId(Long id);

    List<PostDto> findPostPublicationsByUserAuthorId(Long id);

    List<PostDto> findPostPublicationsByProjectAuthorId(Long id);

    void verifyPost(List<Post> posts);
}
