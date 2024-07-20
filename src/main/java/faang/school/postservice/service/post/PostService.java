package faang.school.postservice.service.post;

import faang.school.postservice.entity.dto.post.PostCreateDto;
import faang.school.postservice.entity.dto.post.PostDto;
import faang.school.postservice.entity.dto.post.PostUpdateDto;
import faang.school.postservice.entity.model.Post;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    PostDto getById(Long id);

    void addPostView(Long id);

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

    void correctPosts();

    List<PostDto> findUserFollowingsPosts(Long userId, LocalDateTime date, int limit);
}
