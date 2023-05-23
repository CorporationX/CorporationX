package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.service.filter.goal.GoalFilter;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class AbstractGoalService {

    private final List<GoalFilter> filters;
    protected final GoalMapper goalMapper;

    protected List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filter) {
        filters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filter))
                .forEach(goalFilter -> goalFilter.applyFilter(goals, filter));
        return goals
                .skip((long) filter.getPageSize() * filter.getPage())
                .limit(filter.getPageSize())
                .map(goalMapper::toDto)
                .toList();
    }
}