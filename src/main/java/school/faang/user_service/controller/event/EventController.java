package school.faang.user_service.controller.event;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public List<Event> getParticipatedEventsByUserId(long userId) {
        return eventService.getParticipatedEventsByUserId(userId);
    }
    public Event getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

}
