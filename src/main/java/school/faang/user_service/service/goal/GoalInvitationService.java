package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
=======
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFoundException;
>>>>>>> BJS2-1569
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
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

    public GoalInvitation getGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("GoalInvitation by id: " + id + " is not found"));
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = getGoalInvitationById(id);

        User invitedUser = goalInvitation.getInvited();

        goalInvitationValidator.validateGoal(invitedUser, goalInvitation);

        List<Goal> currentUserGoals = invitedUser.getGoals();
        currentUserGoals.add(goalInvitation.getGoal());

        invitedUser.setGoals(currentUserGoals);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);

    @SneakyThrows
    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = invitationMapper.toEntity(invitation);

        goalInvitationValidator.checkUser(invitation.getInviterId(), invitation.getInvitedUserId());
        goalInvitationRepository.save(goalInvitation);

        return invitationMapper.toDto(goalInvitation);
    }
}
