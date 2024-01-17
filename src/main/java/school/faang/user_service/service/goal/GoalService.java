package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public boolean existsGoalById(long id) {
        return goalRepository.existsById(id);
    }
}
