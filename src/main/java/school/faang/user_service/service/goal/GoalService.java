package school.faang.user_service.service.goal;

import school.faang.user_service.dto.GoalDto;

import java.util.List;

public interface GoalService {
    void deleteById(long goalId);
    List<GoalDto> findGoalsByUserId(long userId);
}