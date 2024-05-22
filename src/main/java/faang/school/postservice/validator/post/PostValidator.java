package faang.school.postservice.validator.post;

import faang.school.postservice.model.Post;

public interface PostValidator {

    void validateAuthor(Long userId, Long projectId);

    void validatePostContent(String content);

    void validatePublicationPost(Post post);
}
