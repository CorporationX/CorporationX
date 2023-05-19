package school.faang.user_service.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/event")
    public EventDto create(@RequestBody @Validated EventDto event) {
        return eventService.create(event);
    }

    @GetMapping("/event/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @PostMapping("/event/list")
    public List<EventDto> getEventsByFilter(@RequestBody @Validated EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping("/event/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping("/event/{id}")
    public EventDto updateEvent(@RequestBody @Validated EventDto event) {
        return eventService.updateEvent(event);
    }

    @GetMapping("/event/{userId}/owned")
    public List<EventDto> getOwnedEvents(@PathVariable long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/event/{userId}/participated")
    public List<EventDto> getParticipatedEvents(@PathVariable long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}