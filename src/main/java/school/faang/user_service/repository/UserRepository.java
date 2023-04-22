package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
