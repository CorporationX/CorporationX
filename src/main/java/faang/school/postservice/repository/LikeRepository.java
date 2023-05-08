package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

    void deleteByPostIdAndUserId(long postId, long userId);

    void deleteByCommentIdAndUserId(long commentId, long userId);
}
