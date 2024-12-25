package school.faang.user_service.repository.mentorship;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.User;

public interface MentorshipRepository extends CrudRepository<User, Long> {
}
