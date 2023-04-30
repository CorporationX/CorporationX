package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.EventRepository;
import school.faang.user_service.service.filter.event.EventFilter;
import school.faang.user_service.validator.EventValidator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class EventService extends AbstractEventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;

    public EventService(EventRepository eventRepository, List<EventFilter> filters,
                        EventMapper eventMapper, EventValidator eventValidator) {
        super(filters, eventMapper);
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventValidator = eventValidator;
    }

    @Transactional
    public EventDto create(EventDto eventDto) {
        eventValidator.checkIfUserHasSkills(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    @Transactional
    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.checkIfUserHasSkills(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public EventDto getEvent(long id) {
        Event entity = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.EVENT_NOT_FOUND, id));
        return eventMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> events = StreamSupport.stream(eventRepository.findAll().spliterator(), false);
        return filterEvents(events, filter);
    }

    @Transactional
    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream().map(eventMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream().map(eventMapper::toDto).toList();
    }
}