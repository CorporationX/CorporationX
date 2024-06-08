package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventRepository;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public void deleteById(long eventId) {
       if (!existsById(eventId)) {
           throw new EntityNotFoundException(String.format("event with id=%d not found", eventId));
       }
        eventRepository.deleteById(eventId);
    }

    private boolean existsById(long eventId) {
        return eventRepository.existsById(eventId);
    }
}