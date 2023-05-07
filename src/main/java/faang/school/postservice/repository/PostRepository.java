package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(Long authorId);

    List<Post> findByProjectId(Long projectId);
}
