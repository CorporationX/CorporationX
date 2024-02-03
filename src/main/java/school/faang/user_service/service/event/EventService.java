package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final UserService userService;
    private final List<EventFilter> eventFilters;

    public EventDto updateEvent(EventDto eventDto) {
        Event event = getEvent(eventDto.getId());
        eventValidator.validateEventToUpdate(eventDto);
        User owner = userService.findUserById(eventDto.getOwnerId());
        event.setOwner(owner);
        event.setStartDate(eventDto.getStartDate());

        return eventMapper.toDto(eventRepository.save(event));
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    public EventDto create(EventDto eventDto) {
        eventValidator.checkIfOwnerExistsById(eventDto.getOwnerId());
        eventValidator.checkIfOwnerHasSkillsRequired(eventDto);
        Event eventEntity = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public List<EventDto> getParticipatedEventsByUserId(long userId) {
        List<Event> participatedEventsByUserId = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toListDto(participatedEventsByUserId);
    }

    public Event getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Not found event by Id - " + eventId));
    }

    public EventDto getEventDto(long eventId) {
        Event event = getEvent(eventId);
        return eventMapper.toDto(event);
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        Stream<Event> events = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(filterDto)) {
                events = eventFilter.apply(events, filterDto);
            }
        }
        return eventMapper.toListDto(events.collect(Collectors.toList()));
    }

}
