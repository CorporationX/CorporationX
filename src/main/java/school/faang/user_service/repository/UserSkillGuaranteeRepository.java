package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserSkillGuarantee;

@Repository
public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {
}