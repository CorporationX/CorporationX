package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.goal.GoalInvitation;

public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {
}