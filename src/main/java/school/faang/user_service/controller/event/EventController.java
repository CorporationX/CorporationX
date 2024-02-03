package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        eventValidator.validateEventInController(eventDto);
        return eventService.create(eventDto);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateEventInController(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getParticipatedEventsByUserId(long userId) {
        return eventService.getParticipatedEventsByUserId(userId);
    }

    public EventDto getEvent(long eventId) {
        return eventService.getEventDto(eventId);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

}