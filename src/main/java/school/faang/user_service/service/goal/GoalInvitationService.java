package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper invitationMapper;
    private final UserService userService;

    @SneakyThrows
    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = invitationMapper.toEntity(invitation);

        User inviter = userService.findUserById(invitation.getInviterId())
                .orElseThrow(() -> new EntityNotFoundException("Inviter user not found"));
        User invited = userService.findUserById(invitation.getInvitedUserId())
                .orElseThrow(() -> new EntityNotFoundException("Invited user not found"));

        if (checkUser(inviter, invited)) {
            goalInvitationRepository.save(goalInvitation);
        }

        return invitationMapper.toDto(goalInvitation);
    }

    @SneakyThrows
    private boolean checkUser(User inviter, User invited) {
        if (!userService.existsUserById(inviter.getId())) {
            throw new EntityNotFoundException("User does not exists");
        } else if (!userService.existsUserById(inviter.getId())) {
            throw new EntityNotFoundException("User does not exists");
        } else if (inviter.getId() == invited.getId()) {
            throw new EntityNotFoundException("This user is the same");
        } else {
            return true;
        }
    }
}
