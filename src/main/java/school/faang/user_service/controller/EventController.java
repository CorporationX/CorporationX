package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/event")
    public EventDto create(@RequestBody EventDto event) {
        if (validateEvent(event)) {
            return eventService.create(event);
        }
        throw new DataValidationException(ErrorMessage.INVALID_EVENT);
    }

    @GetMapping("/event/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @PostMapping("/event/list")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping("/event/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping("/event/{id}")
    public EventDto updateEvent(@RequestBody EventDto event) {
        if (validateEvent(event)) {
            return eventService.updateEvent(event);
        }
        throw new DataValidationException(ErrorMessage.INVALID_EVENT);
    }

    @GetMapping("/event/{userId}/owned")
    public List<EventDto> getOwnedEvents(@PathVariable long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/event/{userId}/participated")
    public List<EventDto> getParticipatedEvents(@PathVariable long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    private boolean validateEvent(EventDto event) {
        return event.getTitle() != null &&
                !event.getTitle().isBlank() &&
                event.getStartDate() != null &&
                event.getOwnerId() != null;
    }
}