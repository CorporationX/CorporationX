package school.faang.user_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends CrudRepository<Skill, Long> {

    @Query(nativeQuery = true, value = "INSERT INTO skill (title) VALUES (:title)")
    @Modifying
    Skill create(String title);

    boolean existsByTitle(String title);

    @Query(nativeQuery = true, value = "SELECT COUNT(id) FROM skill WHERE id IN (:ids) GROUP BY id")
    int countExisting(List<Long> ids);

    Page<Skill> findAllByUserId(long userId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill
            JOIN skill_offer so ON so.skill_id = s.id
            JOIN recommendation r ON r.id = so.recommendation_id
            WHERE r.receiver_id = :userId
            """, countQuery = """
            SELECT COUNT(s.id) FROM skill
            JOIN skill_offer so ON so.skill_id = s.id
            JOIN recommendation r ON r.id = so.recommendation_id
            WHERE r.receiver_id = :userId
            GROUP BY s.id
            """)
    Page<Skill> findSkillsOfferedToUser(long userId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill
            JOIN user_skill us ON us.skill_id = :skillId AND us.user_id = :userId
            """)
    Optional<Skill> findUserSkill(long skillId, long userId);

    @Query(nativeQuery = true, value = "INSERT INTO user_skill (skill_id, user_id) VALUES (:skillId, :userId)")
    @Modifying
    void assignSkillToUser(long skillId, long userId);
}
