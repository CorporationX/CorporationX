package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventService {
    void deleteById(long eventId);
    EventDto findById(Long eventId);
    Event findEventById(Long eventId);
    EventDto createEvent(EventDto eventDto);
    List<EventDto> findAll();
}