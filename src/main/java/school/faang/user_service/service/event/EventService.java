package school.faang.user_service.service.event;

import school.faang.user_service.dto.EventDto;

import java.util.List;

public interface EventService {
    EventDto findById(Long eventId);
    EventDto createEvent(EventDto eventDto);
    List<EventDto> findAll();
}