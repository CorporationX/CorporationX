package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/{userId}/goals")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, @RequestBody GoalFilterDto filter) {
        return goalService.findGoalsByUserId(userId, filter);
    }

    @PostMapping("/{userId}/goal")
    public GoalDto createGoal(@PathVariable Long userId, @RequestBody GoalDto goal) {
        if (validate(goal)) {
            return goalService.createGoal(userId, goal);
        }
        throw new DataValidationException(ErrorMessage.INVALID_GOAL_PROVIDED);
    }

    @PutMapping("/goal/{goalId}")
    public GoalDto updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goal) {
        if (validate(goal)) {
            return goalService.updateGoal(goalId, goal);
        }
        throw new DataValidationException(ErrorMessage.INVALID_GOAL_PROVIDED);
    }

    @DeleteMapping("/goal/{goalId}")
    public void deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @PostMapping("/goal/{goalId}/subtasks")
    public List<GoalDto> getSubtasksByGoal(@PathVariable Long goalId, @RequestBody GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    private boolean validate(GoalDto goal) {
        return goal.getTitle() != null && !goal.getTitle().isBlank();
    }
}