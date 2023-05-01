package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Goal;

import java.util.stream.Stream;

@Repository
public interface GoalRepository extends CrudRepository<Goal, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM goal g
            JOIN user_goal ug ON g.id = ug.goal_id
            WHERE ug.user_id = :userId
            """)
    Stream<Goal> findGoalsByUserId(long userId);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(ug.goal_id) FROM user_goal ug
            JOIN goal g ON g.id = ug.goal_id
            WHERE ug.user_id = :userId AND g.status = 'ACTIVE'
            """)
    int countActiveGoalsPerUser(long userId);

    @Query(nativeQuery = true, value = """
            WITH RECURSIVE subtasks AS (
            SELECT * FROM goal WHERE id = :goalId
            UNION
            SELECT g.* FROM goal g
            JOIN subtasks st ON st.id = g.parent_id
            )
            SELECT * FROM subtasks WHERE id != :goalId
            """)
    Stream<Goal> findByParent(long goalId);
}