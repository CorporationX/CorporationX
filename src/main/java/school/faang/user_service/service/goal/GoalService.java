package school.faang.user_service.service.goal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.AbstractGoalService;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.filter.goal.GoalFilter;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class GoalService extends AbstractGoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final SkillService skillService;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public GoalService(GoalRepository goalRepository, UserSkillGuaranteeRepository userSkillGuaranteeRepository,
                       SkillService skillService, GoalMapper goalMapper,
                       List<GoalFilter> filters, GoalValidator validator) {
        super(filters, goalMapper);
        this.goalRepository = goalRepository;
        this.skillService = skillService;
        this.goalMapper = goalMapper;
        this.goalValidator = validator;
        this.userSkillGuaranteeRepository = userSkillGuaranteeRepository;
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
        setGoalToUser(userId, goal);
        Goal savedGoal = goalRepository.save(goalMapper.toEntity(goal));
        return goalMapper.toDto(savedGoal);
    }

    @Transactional
    public GoalDto setGoalToMentee(long mentorId, long menteeId, GoalDto goal) {
        goalValidator.validate(mentorId, menteeId, goal);
        goal.setMentorId(mentorId);
        setGoalToUser(menteeId, goal);
        Goal savedGoal = goalRepository.save(goalMapper.toEntity(goal));
        return goalMapper.toDto(savedGoal);
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = findGoalById(goalId);
        goalValidator.validateBeforeUpdate(goal, goalDto);
        assignSkillsToUsers(goal, goalDto);
        Goal updatedGoal = goalMapper.toEntity(goalDto);
        return goalMapper.toDto(goalRepository.save(updatedGoal));
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

    @Transactional
    public Goal findGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.GOAL_NOT_FOUND, goalId));
    }

    private void assignSkillsToUsers(Goal goal, GoalDto updatedGoal) {
        if (isRequiredToAssign(goal, updatedGoal)) {
            goal.getUsers().forEach(user ->
                    goal.getSkillsToAchieve().forEach(skill -> {
                                addSkillToUser(skill, user);
                                addMentorToGuarantees(goal, skill, user);
                            }
                    ));
        }
    }

    private boolean isRequiredToAssign(Goal goal, GoalDto updatedGoal) {
        return updatedGoal.getStatus().equals(GoalStatus.COMPLETED) &&
                !goal.getSkillsToAchieve().isEmpty();
    }

    @Transactional
    public void setGoalToUser(long userId, GoalDto goal) {
        List<Long> ids = new ArrayList<>();
        ids.add(userId);
        goal.setUserIds(ids);
    }

    private void addMentorToGuarantees(Goal goal, Skill skill, User mentee) {
        if (goal.getMentor() != null) {
            UserSkillGuarantee guarantee = new UserSkillGuarantee();
            guarantee.setGuarantor(goal.getMentor());
            guarantee.setUser(mentee);
            guarantee.setSkill(skill);
            userSkillGuaranteeRepository.save(guarantee);
        }
    }

    private void addSkillToUser(Skill skill, User user) {
        if (!skill.getUsers().contains(user))
            skill.getUsers().add(user);
    }
}