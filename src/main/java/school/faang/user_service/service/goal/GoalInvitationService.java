package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper invitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;

    @SneakyThrows
    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        GoalInvitation goalInvitation = invitationMapper.toEntity(invitation);

        goalInvitationValidator.checkUser(invitation.getInviterId(), invitation.getInvitedUserId());
        goalInvitationRepository.save(goalInvitation);

        return invitationMapper.toDto(goalInvitation);
    }


}
