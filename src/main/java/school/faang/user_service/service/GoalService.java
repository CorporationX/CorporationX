package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Goal;
import school.faang.user_service.entity.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filter.goal.GoalFilter;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class GoalService extends AbstractGoalService {

    private static final int MAX_ACTIVE_GOALS_SIMULTANEOUSLY = 3;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final SkillRepository skillRepository;

    public GoalService(GoalRepository goalRepository, SkillRepository skillRepository, GoalMapper goalMapper,
                       List<GoalFilter> filters, GoalValidator validator) {
        super(filters, goalMapper);
        this.goalRepository = goalRepository;
        this.skillRepository = skillRepository;
        this.goalMapper = goalMapper;
        this.goalValidator = validator;
    }

    @Transactional
    public List<GoalDto> findGoalsByUserId(long userId, GoalFilterDto filter) {
        Stream<Goal> goals = StreamSupport.stream(goalRepository.findGoalsByUserId(userId).spliterator(), false);
        return filterGoals(goals, filter);
    }

    public GoalDto createGoal(long userId, GoalDto goal) {
        int currentGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
        if (currentGoalsCount < MAX_ACTIVE_GOALS_SIMULTANEOUSLY) {
            Goal savedGoal = goalRepository.save(goalMapper.toEntity(goal));
            return goalMapper.toDto(savedGoal);
        }
        throw new DataValidationException(ErrorMessage.TOO_MANY_GOALS, MAX_ACTIVE_GOALS_SIMULTANEOUSLY);
    }

    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.GOAL_NOT_FOUND, goalId));
        goalDto.setId(goalId);
        goalValidator.validate(goal, goalDto);
        assignSkillsToUser(goal, goalDto);
        Goal savedGoal = goalRepository.save(goalMapper.toEntity(goalDto));
        return goalMapper.toDto(savedGoal);
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        Stream<Goal> subtasks = StreamSupport.stream(goalRepository.findByParent(goalId).spliterator(), false);
        return filterGoals(subtasks, filter);
    }

    private void assignSkillsToUser( Goal goal, GoalDto updatedGoal) {
        if (goal.getStatus().equals(GoalStatus.ACTIVE) &&
                updatedGoal.getStatus().equals(GoalStatus.COMPLETED) &&
                !goal.getSkillsToAchieve().isEmpty()) {
            updatedGoal.getSkillIds().forEach(skill ->
                    skillRepository.assignSkillToUser(skill, updatedGoal.getUserId()));
        }
    }
}