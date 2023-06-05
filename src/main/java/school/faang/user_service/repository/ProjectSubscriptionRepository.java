package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.ProjectSubscription;

@Repository
public interface ProjectSubscriptionRepository extends CrudRepository<ProjectSubscription, Long> {
}
