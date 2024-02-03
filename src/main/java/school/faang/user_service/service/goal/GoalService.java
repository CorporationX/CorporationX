package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalRepository;

/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    public Goal getGoalBbyId(long id) {
        return goalRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Goal with id - " + id + " not found"));
    }

    public void existsGoalById(long id) {
        if (!goalRepository.existsById(id)) {
            throw new EntityNotFoundException("Goal not found");
        }
    }
}
