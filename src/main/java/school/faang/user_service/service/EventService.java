package school.faang.user_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.exception.NotFoundException;
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
    public EventDto create(EventDto event) {
        eventValidator.validateSkills(event);
        Event entity = eventRepository.create(
                event.getTitle(),
                event.getStartDate(),
                event.getOwner().getId(),
                event.getDescription(),
                event.getEndDate(),
                event.getLocation(),
                event.getMaxAttendees()
        );
        return eventMapper.toDto(entity);
    }

    @Transactional
    public EventDto getEvent(long id) {
        Event entity = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND, id));
        return eventMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEvents(EventFilterDto filter) {
        Stream<Event> events = StreamSupport.stream(eventRepository.findAll().spliterator(), false);
        return filterEvents(events, filter);
    }

    @Transactional
    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    @Transactional
    public EventDto updateEvent(long eventId, EventDto event) {
        eventValidator.validateSkills(event);
        Event entity = eventRepository.update(
                eventId,
                event.getTitle(),
                event.getStartDate(),
                event.getOwner().getId(),
                event.getDescription(),
                event.getEndDate(),
                event.getLocation(),
                event.getMaxAttendees()
        );
        return eventMapper.toDto(entity);
    }

    @Transactional
    public Page<EventDto> getOwnedEvents(long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return eventRepository.findAllByUserId(userId, pageable).map(eventMapper::toDto);
    }

    @Transactional
    public Page<EventDto> getParticipatedEvents(long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return eventRepository.findParticipatedEventsByUserId(userId, pageable).map(eventMapper::toDto);
    }

    @Transactional
    public void addSkillsToEvent(long ownerId, long eventId, List<SkillDto> skills) {
        eventValidator.validate(ownerId, eventId, skills);
        skills.forEach(skill -> eventRepository.addSkillsToEvent(skill.getId(), eventId));
    }
}