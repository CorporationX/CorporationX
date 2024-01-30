package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final UserService userService;

    public EventDto updateEvent(EventDto eventDto) {
        Event event = checkIfEventExists(eventDto.getId());
        checkIfEventNotStarted(event.getStartDate());
        userService.checkIfOwnerExists(eventDto.getOwnerId());
        eventValidator.checkIfOwnerHasSkillsRequired(eventDto);
        User owner = userService.findUserById(eventDto.getOwnerId());
        event.setOwner(owner);
        event.setStartDate(eventDto.getStartDate());

        return eventMapper.toDto(eventRepository.save(event));
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    public Event checkIfEventExists(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Event with ID %d not found", id)));
    }

    public EventDto create(EventDto eventDto) {
        Event eventEntity = eventMapper.toEntity(eventDto);
        eventValidator.checkIfOwnerExistsById(eventEntity.getOwner().getId());
        eventValidator.checkIfOwnerHasSkillsRequired(eventEntity);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public List<Event> getParticipatedEventsByUserId(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    public Event getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Not found event by Id - " + eventId));
}

public void checkIfEventNotStarted(LocalDateTime startDate) {
    if (startDate.isBefore(LocalDateTime.now())) {
        throw new DataValidationException("Event were started");
    }
}
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

}

}