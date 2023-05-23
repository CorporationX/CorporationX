package school.faang.user_service.service.filter.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

public abstract class GoalFilter {

    public Stream<Goal> applyFilter(Stream<Goal> goals, GoalFilterDto filter) {
        return goals.filter(goal -> applyFilter(goal, filter));
    }

    protected abstract boolean applyFilter(Goal goal, GoalFilterDto filter);

    public abstract boolean isApplicable(GoalFilterDto filter);
}