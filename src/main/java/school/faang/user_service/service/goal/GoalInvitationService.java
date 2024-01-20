package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.DataValidationException;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserService userService;
    private final GoalService goalService;
    private final List<Filter<InvitationFilterDto, GoalInvitation>> goalInvitationFilters;

    @SneakyThrows
    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GoalInvitation is not found"));

        List<GoalInvitation> receivedGoalInvitations = new ArrayList<>();
        receivedGoalInvitations.add(goalInvitation);

        List<Goal> goals = new ArrayList<>();
        goals.add(goalInvitation.getGoal());

        User invitedUser = goalInvitation.getInvited();

        if (checkData(invitedUser, goalInvitation)) {
            invitedUser.setReceivedGoalInvitations(receivedGoalInvitations);
            invitedUser.setGoals(goals);
        }
    }

    @SneakyThrows
    public void rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal invitation is not found"));
        Goal goal = goalInvitation.getGoal();

        if (checkGoalIsExist(goal)) {
            goalInvitationRepository.delete(goalInvitation);
        }
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll();

        if (checkFilter(filter)) {
            return new ArrayList<>(goalInvitations.stream().map(goalInvitationMapper::toDto).toList());
        }

        List<List<GoalInvitation>> filteredInvitations = goalInvitationFilters.stream()
                .filter(goalInvitationFilter -> goalInvitationFilter.isApplicable(filter))
                .map(goalInvitationFilter -> goalInvitationFilter.apply(goalInvitations, filter))
                .toList();

        List<GoalInvitation> filteredGoalInvitations = filteredInvitations.stream()
                .flatMap(Collection::stream)
                .toList();

        return new ArrayList<>(filteredGoalInvitations.stream()
                .map(goalInvitationMapper::toDto).toList());
    }

    @SneakyThrows
    private boolean checkGoalIsExist(Goal goal) {
        if (!goalService.existsGoalById(goal.getId())) {
            throw new EntityNotFoundException("Goal not found");
        }
        return true;
    }

    @SneakyThrows
    private boolean checkData(User user, GoalInvitation goalInvitation) {
        if (!userService.existsUserById(user.getId())) {
            throw new EntityNotFoundException("User is not found");
        } else if (!goalInvitationRepository.existsById(goalInvitation.getId())) {
            throw new EntityNotFoundException("GoalInvitation is not found");
        } else if (user.getGoals().contains(goalInvitation.getGoal())) {
            throw new DataValidationException("User already exist this goal");
        } else if (!(user.getGoals().size() < MAX_ACTIVE_GOALS)) {
            throw new DataValidationException("User already have maximum active goals");
        } else {
            return true;
        }
    }

    private boolean checkFilter(InvitationFilterDto filter) {
        return filter.getInviterNamePattern() == null && filter.getInvitedNamePattern() == null
                && filter.getInviterId() == null && filter.getInvitedId() == null;
    }
}
