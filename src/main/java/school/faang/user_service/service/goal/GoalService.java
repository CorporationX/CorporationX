package school.faang.user_service.service.goal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.AbstractGoalService;
import school.faang.user_service.service.filter.goal.GoalFilter;
import school.faang.user_service.validator.GoalValidator;

import java.util.*;
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

    @Transactional
    public GoalDto createGoal(long userId, GoalDto goal) {
        int currentGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
        if (currentGoalsCount < MAX_ACTIVE_GOALS_SIMULTANEOUSLY) {
            Goal savedGoal = goalRepository.save(goalMapper.toEntity(goal));
            return goalMapper.toDto(savedGoal);
        }
        throw new DataValidationException(ErrorMessage.TOO_MANY_GOALS, MAX_ACTIVE_GOALS_SIMULTANEOUSLY);
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = findGoal(goalId);
        goalDto.setId(goalId);
        goalValidator.validate(goal, goalDto);
        assignSkillsToUsers(goal, goalDto);
        Goal savedGoal = goalRepository.save(goalMapper.toEntity(goalDto));
        return goalMapper.toDto(savedGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        Stream<Goal> subtasks = StreamSupport.stream(goalRepository.findByParent(goalId).spliterator(), false);
        return filterGoals(subtasks, filter);
    }

    @Transactional
    public List<User> findUsersByGoalId(long goalId) {
        return goalRepository.findUsersByGoalId(goalId);
    }

    @Transactional(readOnly = true)
    public int countUsersSharingGoal(long goalId) {
        return goalRepository.countUsersSharingGoal(goalId);
    }

    @Transactional
    public void findGoalById(long goalId) {
        findGoal(goalId);
    }

    private void assignSkillsToUsers(Goal goal, GoalDto updatedGoal) {
        if (goal.getStatus().equals(GoalStatus.ACTIVE) &&
                updatedGoal.getStatus().equals(GoalStatus.COMPLETED) &&
                !goal.getSkillsToAchieve().isEmpty()) {
            updatedGoal.getUserIds().forEach(userId ->
                    updatedGoal.getSkillIds().forEach(skillId ->
                            skillRepository.assignSkillToUser(skillId, userId)));
        }
    }

    private Goal findGoal(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.GOAL_NOT_FOUND, goalId));
    }
}