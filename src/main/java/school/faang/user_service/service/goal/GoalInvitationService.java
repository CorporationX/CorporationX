package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.DataValidationException;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper invitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final UserService userService;
    private final GoalService goalService;
    private final List<Filter<InvitationFilterDto, GoalInvitation>> goalInvitationFilters;

    @SneakyThrows
    public GoalInvitation getGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GoalInvitation is not found"));
    }

    @SneakyThrows
    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = invitationMapper.toEntity(invitation);

        User inviter = userService.getUserById(invitation.getInviterId());
        User invited = userService.getUserById(invitation.getInvitedUserId());

        if (goalInvitationValidator.checkUser(inviter, invited)) {
            goalInvitationRepository.save(goalInvitation);
        }

        return invitationMapper.toDto(goalInvitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = getGoalInvitationById(id);

        User invitedUser = goalInvitation.getInvited();

        List<GoalInvitation> currentUserReceivedInvitations = invitedUser.getReceivedGoalInvitations();
        currentUserReceivedInvitations.add(goalInvitation);


        if (goalInvitationValidator.checkData(invitedUser, goalInvitation)) {
            List<Goal> currentUserGoals = invitedUser.getGoals();
            currentUserGoals.add(goalInvitation.getGoal());

            invitedUser.setReceivedGoalInvitations(currentUserReceivedInvitations);
            invitedUser.setGoals(currentUserGoals);

            goalInvitation.setStatus(RequestStatus.ACCEPTED);

            userService.saveUser(invitedUser);
            goalInvitationRepository.save(goalInvitation);
        }
        return invitationMapper.toDto(goalInvitation);
    }

    @SneakyThrows
    public void rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = getGoalInvitationById(id);
        Goal goal = goalInvitation.getGoal();

        if (goalService.existsGoalById(goal.getId())) {
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

        Set<GoalInvitation> filteredGoalInvitations = filteredInvitations.stream()
                .flatMap(Collection::stream).collect(Collectors.toSet());

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
