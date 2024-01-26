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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;

    public GoalInvitation getGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("GoalInvitation by id: " + id + " is not found"));
    }
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserService userService;
    private final GoalService goalService;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = invitationMapper.toEntity(invitation);
    public GoalInvitation getGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GoalInvitation is not found"));
    }

        goalInvitationValidator.checkUser(invitation.getInviterId(), invitation.getInvitedUserId());
        goalInvitationRepository.save(goalInvitation);
    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = getGoalInvitationById(id);
        goalService.existsGoalById(goalInvitation.getGoal().getId());

        return invitationMapper.toDto(goalInvitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = getGoalInvitationById(id);
        User invited = goalInvitation.getInvited();

        List<GoalInvitation> currentReceivedGoalInvitations = invited.getReceivedGoalInvitations();
        currentReceivedGoalInvitations.remove(goalInvitation);

        goalInvitationValidator.validateGoal(invitedUser, goalInvitation);

        List<Goal> currentUserGoals = invitedUser.getGoals();
        currentUserGoals.add(goalInvitation.getGoal());

        invitedUser.setGoals(currentUserGoals);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        invited.setReceivedGoalInvitations(currentReceivedGoalInvitations);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        userService.saveUser(invited);
        goalInvitationRepository.save(goalInvitation);

        goalInvitationRepository.save(goalInvitation);

        return invitationMapper.toDto(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitation);
    }
}
