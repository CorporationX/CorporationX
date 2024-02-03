package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.goal.GoalInvitation;

@Repository
public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {
}