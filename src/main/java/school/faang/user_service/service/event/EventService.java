package school.faang.user_service.service.event;

import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventService {
    EventDto findById(Long eventId);
    Event findEventById(Long eventId);
    EventDto createEvent(EventDto eventDto);
    List<EventDto> findAll();
    void deleteById(long eventId);
}