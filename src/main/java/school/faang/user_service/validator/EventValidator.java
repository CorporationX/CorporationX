package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.EventParticipationService;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final UserService userService;
    private final EventService eventService;
    private final EventParticipationService eventParticipationService;

    public void validateSkills(EventDto event) {
        checkIfUserHasSkills(event.getOwner().getId(), event.getRelatedSkills());
    }

    public boolean isUserRegistered(long eventId, long userId) {
        eventService.getEvent(eventId);
        List<UserDto> participants = eventParticipationService.getParticipants(eventId);
        return participants.stream().anyMatch(user -> user.getId() == userId);
    }

    public void validate(long userId, long eventId, List<SkillDto> skills) {
        EventDto event = eventService.getEvent(eventId);
        long eventOwnerId = event.getOwner().getId();
        if (eventOwnerId != userId) {
            throw new DataValidationException(ErrorMessage.INAPPLICABLE_USER);
        }
        checkIfUserHasSkills(eventOwnerId, skills);
    }

    public void checkIfUserHasSkills(long ownerId, List<SkillDto> skills) {
        List<Long> skillIds = skills.stream()
                .map(SkillDto::getId)
                .toList();
        if (!userService.areOwnedSkills(ownerId, skillIds)) {
            throw new DataValidationException(ErrorMessage.INVALID_USER_SKILLS);
        }
    }
}