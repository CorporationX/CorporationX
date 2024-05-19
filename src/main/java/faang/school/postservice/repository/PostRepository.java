package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("""
            SELECT p FROM Post p\
            WHERE p.published = :published AND p.deleted = :deleted\
            LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId
            """)
    List<Post> findByAuthorIdAndPublishedAndDeletedWithLikes(Long authorId, boolean published, boolean deleted);

    @Query("""
            SELECT p FROM Post p\
            WHERE p.published = :published AND p.deleted = :deleted\
            LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId
            """)
    List<Post> findByProjectIdAndPublishedAndDeletedWithLikes(Long projectId, boolean published, boolean deleted);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.id = :postId")
    Optional<Post> findByIdWithLikes(long postId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();
}
