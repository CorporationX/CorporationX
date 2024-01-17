package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<Event> getParticipatedEventsByUserId(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

}
