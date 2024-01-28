package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final UserService userService;

    public void validateEventInController(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank()
                || event.getStartDate().isBefore(LocalDateTime.now())) {
            throw new DataValidationException("Event not valid");
        }
    }

    public void checkIfOwnerExistsById(long id) {
        if (!userService.checkIfOwnerExistsById(id)) {
            throw new DataValidationException("Owner does not exist");
        }
    }

    public void checkIfOwnerHasSkillsRequired(Event event) {
        boolean ownerHasRequiredSkills = new HashSet<>(event
                .getOwner()
                .getSkills())
                .containsAll(event.getRelatedSkills());
        if (!ownerHasRequiredSkills) {
            throw new DataValidationException("Owner does not have required skills");
        }
    }
}
