package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final List<EventFilter> eventFilters;

    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    public EventDto create(EventDto eventDto) {
        Event eventEntity = eventMapper.toEntity(eventDto);
        eventValidator.checkIfOwnerExistsById(eventEntity.getOwner().getId());
        eventValidator.checkIfOwnerHasSkillsRequired(eventEntity);
        return eventMapper.toDto(eventRepository.save(eventEntity));
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

    public List<Event> getParticipatedEventsByUserId(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    public Event getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Not found event by Id - " + eventId));
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

}
