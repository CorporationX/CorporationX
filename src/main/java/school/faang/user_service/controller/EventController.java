package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.service.EventService;

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

    @GetMapping("/event/list")
    public List<EventDto> getEvents(@RequestBody EventFilterDto filter) {
        return eventService.getEvents(filter);
    }

    @DeleteMapping("/event/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping("/event/{id}")
    public EventDto updateEvent(@PathVariable Long id, @RequestBody EventDto event) {
        if (validateEvent(event)) {
            return eventService.updateEvent(id, event);
        }
        throw new DataValidationException(ErrorMessage.INVALID_EVENT);
    }

    @GetMapping("/event/{userId}/owned")
    public Page<EventDto> getOwnedEvents(@PathVariable long userId, @RequestParam int page, @RequestParam int pageSize) {
        return eventService.getOwnedEvents(userId, page, pageSize);
    }

    @GetMapping("/event/{userId}/participated")
    public Page<EventDto> getParticipatedEvents(@PathVariable long userId, @RequestParam int page, @RequestParam int pageSize) {
        return eventService.getParticipatedEvents(userId, page, pageSize);
    }

    @PostMapping("/{userId}/{eventId}/skills")
    public void addSkillsToEvent(@PathVariable Long ownerId, @PathVariable Long eventId,
                                     @RequestBody List<SkillDto> skills) {
        eventService.addSkillsToEvent(ownerId, eventId, skills);
    }

    private boolean validateEvent(EventDto event) {
        return event.getTitle() != null &&
                !event.getTitle().isBlank() &&
                event.getStartDate() != null &&
                event.getOwner() != null;
    }
}