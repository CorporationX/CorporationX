package school.faang.user_service.repository.premium;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.premium.Premium;

@Repository
public interface PremiumRepository extends CrudRepository<Premium, Long> {

    boolean existsByUserId(long userId);
}
