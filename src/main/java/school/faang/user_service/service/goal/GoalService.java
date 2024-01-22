package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalRepository;

/**
 * @author Ilia Chuvatkin
 */


/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    @SneakyThrows
    public boolean existsGoalById(long id) {
        if (!goalRepository.existsById(id)) {
            throw new EntityNotFoundException("Goal not found");
        }
        return true;
    }
}
