package school.faang.user_service.controller.event;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

@Component
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        if (!eventValidator.validateEventInController(eventDto)) {
            throw new DataValidationException("Incorrect event.");
        }
        return eventService.create(eventDto);
    }

}
