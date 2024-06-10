package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public EventDto findById(Long eventId) {
        return eventMapper.toDto(findEventById(eventId));
    }

    @Override
    public EventDto createEvent(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public List<EventDto> findAll() {
        return eventMapper.toDtoList(eventRepository.findAll());
    }

    @Override
    public void deleteById(long eventId) {
        if (!existById(eventId)) {
            throw new EntityNotFoundException(String.format("event with id=%d not found", eventId));
        }
        eventRepository.deleteById(eventId);
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("event with id = %d not found", eventId)));
    }

    private boolean existById(long eventId) {
        return eventRepository.existsById(eventId);
    }
}