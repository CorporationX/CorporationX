package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class EventValidator {

    public void validateEventInController(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank()
                || event.getStartDate().isBefore(LocalDateTime.now())) {
            throw new DataValidationException("Event not valid");
        }
    }

    public void checkIfOwnerHasSkillsRequired(Event event) {
        if (!new HashSet<>(event.getOwner().getSkills()).containsAll(event.getRelatedSkills())) {
            throw new DataValidationException("Owner does not have required skills.");
        }
    }
}