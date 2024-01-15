package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto event) {
        Event eventEntity = eventMapper.toEntity(event);
        validateOwnerHasSkills(eventEntity);
        Event savedEvent = eventRepository.save(eventEntity);
        event.setId(savedEvent.getId());
        return event;
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        if (filters == null) {
            throw new DataValidationException("Not valid filter");
        }
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        return eventMapper.toEventDto(events.toList());
    }

    public void validateOwnerHasSkills(Event event) {
        Optional<User> owner = userRepository.findById(event.getOwner().getId());
        User ownerById = owner
                .orElseThrow(() -> new DataValidationException("Owner by id not found"));

        if (!isOwnerHasEventSkills(event, ownerById)) {
            throw new DataValidationException("Owner doesn't have required skill");
        }
    }

    private boolean isOwnerHasEventSkills(Event event, User ownerById) {
        return new HashSet<>(ownerById.getSkills())
                .containsAll(event.getRelatedSkills());
    }

}
