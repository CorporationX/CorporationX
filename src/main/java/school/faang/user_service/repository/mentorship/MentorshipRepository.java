package school.faang.user_service.repository.mentorship;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.User;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN u.mentors m WHERE m.id = :mentorId")
    List<User> findAllMenteesByMentorId(@Param("mentorId") Long mentorId);
}
