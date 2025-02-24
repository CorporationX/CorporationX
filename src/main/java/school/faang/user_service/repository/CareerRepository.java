package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.Career;

public interface CareerRepository extends JpaRepository<Career, Long> {

}
