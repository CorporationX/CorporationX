package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public void deleteEvent(long eventId) {
        Event event = getEvent(eventId);
        eventRepository.deleteById(event.getId());
    }

    public Event getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found with ID - " + eventId));
    }

}
