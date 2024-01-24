package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.ArrayList;
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
    private final List<Filter<InvitationFilterDto, GoalInvitation>> goalInvitationFilters;

    public GoalInvitation getGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GoalInvitation is not found"));
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll();

        if (!goalInvitationValidator.checkFilter(filter)) {
            return new ArrayList<>(goalInvitations.stream().map(invitationMapper::toDto).toList());
        }

        goalInvitationFilters.stream()
                .filter(goalInvitationFilter -> goalInvitationFilter.isApplicable(filter))
                .forEach(goalInvitationFilter -> goalInvitationFilter.apply(goalInvitations, filter));

        return new ArrayList<>(goalInvitations.stream()
                .map(invitationMapper::toDto).toList());
    }
}
