package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper invitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final UserService userService;

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
}
