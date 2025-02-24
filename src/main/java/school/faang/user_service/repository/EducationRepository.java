package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.Education;

public interface EducationRepository extends JpaRepository<Education, Long> {

}
