package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.filter.goal.GoalInvitationFilter;
import school.faang.user_service.validator.GoalInvitationValidator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationValidator goalInvitationValidator;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<GoalInvitationFilter> filters;

    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidator.validateInvitation(invitation);
        GoalInvitation goalInvitation = goalInvitationRepository.save(goalInvitationMapper.toEntity(invitation));
        return goalInvitationMapper.toDto(goalInvitation);
    }

    @Transactional
    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation invitation = findInvitation(id);
        User invited = invitation.getInvited();
        goalInvitationValidator.validate(invitation);
        invitation.setStatus(RequestStatus.ACCEPTED);
        invited.getGoals().add(invitation.getGoal());
        return goalInvitationMapper.toDto(invitation);
    }

    @Transactional
    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation invitation = findInvitation(id);
        goalInvitationValidator.validate(invitation);
        invitation.setStatus(RequestStatus.REJECTED);
        return goalInvitationMapper.toDto(invitation);
    }

    @Transactional
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        Stream<GoalInvitation> invitations = StreamSupport.stream(goalInvitationRepository.findAll().spliterator(), false);
        filters.stream()
                .filter(invitation -> invitation.isApplicable(filter))
                .forEach(invitation -> invitation.applyFilter(invitations, filter));
        return invitations.skip((long) filter.getPage() * filter.getPageSize())
                .limit(filter.getPageSize())
                .map(goalInvitationMapper::toDto)
                .toList();
    }

    private GoalInvitation findInvitation(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no invitation with id " + id + "."));
    }
}