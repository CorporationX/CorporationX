package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper invitationMapper;
    private final UserRepository userRepository;

    public void createInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = invitationMapper.toEntity(invitation);

        User inviterUser = userRepository.findById(invitation.getInviterId()).orElseThrow();
        User invitedUser = userRepository.findById(invitation.getInvitedUserId()).orElseThrow();

        if (!inviterUser.equals(invitedUser) && userRepository.existsById(inviterUser.getId()) &&
                userRepository.existsById(invitedUser.getId())) {
            goalInvitationRepository.save(goalInvitation);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
