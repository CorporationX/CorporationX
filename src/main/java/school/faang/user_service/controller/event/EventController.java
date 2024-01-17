package school.faang.user_service.controller.event;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public Event getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

}
