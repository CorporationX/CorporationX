package school.faang.user_service.service.goal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.AbstractGoalService;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.filter.goal.GoalFilter;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class GoalService extends AbstractGoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final SkillService skillService;

    public GoalService(GoalRepository goalRepository, SkillService skillService, GoalMapper goalMapper,
                       List<GoalFilter> filters, GoalValidator validator) {
        super(filters, goalMapper);
        this.goalRepository = goalRepository;
        this.skillService = skillService;
        this.goalMapper = goalMapper;
        this.goalValidator = validator;
    }

    @Transactional
    public List<GoalDto> findGoalsByUserId(long userId, GoalFilterDto filter) {
        Stream<Goal> goals = StreamSupport.stream(goalRepository.findGoalsByUserId(userId).spliterator(), false);
        goals.forEach(goal -> {
            List<Skill> skills = skillService.findSkillsByGoalId(goal.getId());
            goal.setSkillsToAchieve(skills);
        });
        return filterGoals(goals, filter);
    }

    @Transactional
    public GoalDto createGoal(long userId, GoalDto goal) {
        goalValidator.validateBeforeCreate(userId, goal);
        Goal savedGoal = goalRepository.create(
                goal.getTitle(),
                goal.getDescription(),
                goal.getParentId());
        addSkillsToGoal(goal.getSkillIds(), goal.getId());
        return goalMapper.toDto(savedGoal);
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = findGoalById(goalId);
        goalValidator.validate(goal, goalDto);
        assignSkillsToUsers(goal, goalDto);
        Goal savedGoal = goalRepository.save(goalMapper.toEntity(goalDto));
        updateSkills(goalDto);
        savedGoal.setSkillsToAchieve(skillService.findSkillsByGoalId(goalId));
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
    public Goal findGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.GOAL_NOT_FOUND, goalId));
    }

    private void assignSkillsToUsers(Goal goal, GoalDto updatedGoal) {
        if (isRequiredToAssign(goal, updatedGoal)) {
            goal.getUsers().forEach(user ->
                    updatedGoal.getSkillIds().forEach(skillId ->
                            skillService.assignSkillToUser(skillId, user.getId())));
        }
    }

    private void updateSkills(GoalDto goalDto) {
        List<Long> skillIds = goalDto.getSkillIds();
        goalRepository.removeSkillsFromGoal(goalDto.getId());
        addSkillsToGoal(skillIds, goalDto.getId());
    }

    public boolean isRequiredToAssign(Goal goal, GoalDto updatedGoal) {
        return updatedGoal.getStatus().equals(GoalStatus.COMPLETED) &&
                !goal.getSkillsToAchieve().isEmpty();
    }

    private void addSkillsToGoal(List<Long> skillIds, Long goalId) {
        skillIds.forEach(skillId -> goalRepository.addSkillToGoal(skillId, goalId));
    }
}