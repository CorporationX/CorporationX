package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final UserService userService;

    public boolean validateEventInController(EventDto event) {
        return (event.getTitle() != null && !event.getTitle().isEmpty())
                && event.getStartDate().isAfter(LocalDateTime.now())
                && event.getOwnerId() != 0;
    }

    public boolean checkIfOwnerHasSkillsRequired(Event event) {
        User owner = userService.findOwnerById(event.getOwner().getId());

        return owner.getSkills().containsAll(event.getRelatedSkills());
    }

}
