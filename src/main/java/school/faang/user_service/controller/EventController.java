package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/event")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/")
    public EventDto createEvent(@RequestBody EventDto eventDto) {
        return eventService.createEvent(eventDto);
    }

    @GetMapping("/{id}")
    public EventDto getEventById(@PathVariable("id") Long id) {
        return eventService.findById(id);
    }

    @GetMapping("/")
    public List<EventDto> getAllEvents() {
        return eventService.findAll();
    }
}