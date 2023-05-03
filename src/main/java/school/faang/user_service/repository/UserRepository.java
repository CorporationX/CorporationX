package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            SELECT COUNT(s.id) FROM user u
            JOIN user_skill us ON u.id = us.user_id
            JOIN skill s ON us.skill_id = s.id
            WHERE u.id = :userId AND s.id IN (:ids)
            """)
    int countOwnedSkills(long userId, List<Long> ids);
}