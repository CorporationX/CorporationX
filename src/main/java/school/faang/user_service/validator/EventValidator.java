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

    public boolean isUserRegistered(long eventId, long userId) {
        eventService.getEvent(eventId);
        List<UserDto> participants = eventParticipationService.getParticipants(eventId);
        return participants.stream().anyMatch(user -> user.getId() == userId);
    }

    public void checkIfUserHasSkills(EventDto event) {
        List<Long> skillIds = event.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .toList();
        if (!userService.areOwnedSkills(event.getOwner().getId(), skillIds)) {
            throw new DataValidationException(ErrorMessage.INVALID_USER_SKILLS);
        }
    }
}