package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public boolean validateEventInController(EventDto event) {
        boolean isExist = (event.getTitle() != null && !event.getTitle().isBlank())
                && event.getStartDate().isAfter(LocalDateTime.now());
        if (!isExist) {
            throw new DataValidationException("Event not valid");
        }
        return true;
    }

    public boolean checkEventIsExistById(long id) {
        boolean isExist = eventRepository.existsById(id);
        if (!isExist) {
            throw new DataValidationException("Event not exist.");
        }
        return true;
    }

    public boolean checkIfOwnerExistsById(long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException("Owner does not exist.");
        }
        return true;
    }

    public boolean checkIfOwnerHasSkillsRequired(Event event) {
        boolean ownerHasRequiredSkills = event.getOwner().getSkills().containsAll(event.getRelatedSkills());
        if (!ownerHasRequiredSkills) {
            throw new DataValidationException("Owner does not have required skills.");
        }
        return true;
    }
}