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

    @PostMapping("/users/{userId}/goals")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, @RequestParam GoalFilterDto filter) {
        return goalService.findGoalsByUserId(userId, filter);
    }

    @PostMapping("{userId}/goal/create")
    public GoalDto createGoal(@PathVariable Long userId, @RequestBody GoalDto goal) {
        if (validate(goal)) {
            return goalService.createGoal(userId, goal);
        }
        throw new DataValidationException(ErrorMessage.INVALID_GOAL_PROVIDED);
    }

    @PutMapping("/goals/{goalId}")
    public GoalDto updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goal) {
        if (validate(goal)) {
            return goalService.updateGoal(goalId, goal);
        }
        throw new DataValidationException(ErrorMessage.INVALID_GOAL_PROVIDED);
    }

    @DeleteMapping("/goals/{goalId}")
    public void deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @PostMapping("/goals/{goalId}/subtasks")
    public List<GoalDto> getSubtasksByGoal(@PathVariable Long goalId, @RequestParam GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    private boolean validate(GoalDto goal) {
        return goal.getTitle() != null && !goal.getTitle().isBlank();
    }
}