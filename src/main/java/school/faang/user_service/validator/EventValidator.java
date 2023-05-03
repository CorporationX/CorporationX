package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final UserService userService;
    private final EventParticipationRepository eventParticipationRepository;

    public boolean checkUserIsRegistered(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public void checkIfUserHasSkillsRequired(EventDto event) {
        List<Long> skillIds = event.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .toList();
        if (!userService.areOwnedSkills(event.getOwnerId(), skillIds)) {
            throw new DataValidationException(ErrorMessage.INVALID_USER_SKILLS);
        }
    }
}