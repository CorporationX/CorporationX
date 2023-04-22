package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.ProjectSubscription;

public interface ProjectSubscriptionRepository extends CrudRepository<ProjectSubscription, Long> {
}
