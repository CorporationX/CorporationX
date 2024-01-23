package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserService userService;
    private final GoalService goalService;

    public GoalInvitation getGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GoalInvitation is not found"));
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = getGoalInvitationById(id);
        goalService.existsGoalById(goalInvitation.getGoal().getId());

        User invited = goalInvitation.getInvited();

        List<GoalInvitation> currentReceivedGoalInvitations = invited.getReceivedGoalInvitations();
        currentReceivedGoalInvitations.remove(goalInvitation);

        invited.setReceivedGoalInvitations(currentReceivedGoalInvitations);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        userService.saveUser(invited);
        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }
}
