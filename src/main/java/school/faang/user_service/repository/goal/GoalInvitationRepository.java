package school.faang.user_service.repository.goal;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.goal.GoalInvitation;

@Repository
public interface GoalInvitationRepository extends CrudRepository<GoalInvitation, Long> {
}