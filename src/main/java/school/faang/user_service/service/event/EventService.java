package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventRepository;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

}
