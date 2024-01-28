package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventOwnerIdPattern;
import school.faang.user_service.filter.event.EventStartDatePattern;
import school.faang.user_service.filter.event.EventTitlePattern;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventServiceTest {
    @Mock
    private EventMapper eventMapper;
    @Mock
    EventRepository eventRepository;
    @Mock
    private EventValidator eventValidator;
    @InjectMocks
    private EventService eventService;

    private List<EventFilter> eventFilters = new ArrayList<>();
    private LocalDateTime startDate;
    private User owner;
    private Skill skill;
    private Event eventFirst;
    private Event eventSecond;
    private EventFilterDto eventFilterDtoId;
    private EventFilterDto eventFilterDtoTitle;
    private EventFilterDto eventFilterDtoStartDate;

    @BeforeEach
    void setUp() {
        eventFilters.add(new EventOwnerIdPattern());
        eventFilters.add(new EventTitlePattern());
        eventFilters.add(new EventStartDatePattern());

        eventService = new EventService(eventRepository, eventMapper, eventValidator, eventFilters);

        startDate = LocalDateTime.now().plusDays(1L);
        owner = User.builder().id(1L).active(true).build();
        skill = Skill.builder().id(1L).build();

        eventFirst = Event.builder()
                .id(1L)
                .title("EventFirst")
                .maxAttendees(2)
                .owner(owner)
                .relatedSkills(List.of(skill))
                .startDate(LocalDateTime.now())
                .build();
        eventSecond = Event.builder()
                .id(1L)
                .title("EventSecond")
                .maxAttendees(2)
                .owner(owner)
                .relatedSkills(List.of(skill))
                .startDate(startDate)
                .build();

        eventFilterDtoId = EventFilterDto.builder()
                .ownerPattern(1L)
                .build();
        eventFilterDtoTitle = EventFilterDto.builder()
                .titlePattern("EventSecond")
                .build();
        eventFilterDtoStartDate = EventFilterDto.builder()
                .startDatePattern(startDate)
                .build();
    }

    @Test
    public void successCreateAndSaveEventWhenTwoPassedValidation() {
        Skill skillFirst = Skill.builder()
                .id(1L)
                .build();
        Skill skillSecond = Skill.builder()
                .id(1L)
                .build();
        User owner = User.builder()
                .id(1L)
                .active(true)
                .skills(List.of(skillFirst, skillSecond))
                .build();
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("EventFirst")
                .ownerId(owner.getId())
                .relatedSkillIds(List.of(skillFirst.getId(), skillSecond.getId()))
                .build();
        Event eventEntity = Event.builder()
                .id(1L)
                .title("EventFirst")
                .owner(owner)
                .relatedSkills(List.of(skillFirst, skillSecond))
                .maxAttendees(2)
                .build();

        when(eventMapper.toEntity(eventDto)).thenReturn(eventEntity);
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        when(eventMapper.toDto(eventEntity)).thenReturn(eventDto);
        doNothing().when(eventValidator).checkIfOwnerExistsById(owner.getId());
        doNothing().when(eventValidator).checkIfOwnerHasSkillsRequired(eventEntity);
        EventDto actual = eventService.create(eventDto);

        verify(eventValidator).checkIfOwnerExistsById(owner.getId());
        verify(eventValidator).checkIfOwnerHasSkillsRequired(eventEntity);
        verify(eventRepository).save(eventEntity);
        assertEquals(eventDto, actual);
    }

    @Test
    public void shouldNotSaveEventWhenOwnerNotExistsById() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .title("EventFirst")
                .build();
        User owner = User.builder()
                .id(22L)
                .active(true)
                .build();
        Event eventEntity = Event.builder()
                .id(1L)
                .owner(owner)
                .maxAttendees(2)
                .build();
        when(eventMapper.toEntity(any(EventDto.class))).thenReturn(eventEntity);
        doThrow(new DataValidationException("Owner does not exist"))
                .when(eventValidator).checkIfOwnerExistsById(anyLong());

        assertThrows(DataValidationException.class,
                () -> eventService.create(eventDto));
    }

    @Test
    public void shouldNotSaveEventWhenOwnerDoesNotHaveRequiredSkills() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .title("EventFirst")
                .build();
        User owner = User.builder()
                .id(22L)
                .active(true)
                .build();
        Event eventEntity = Event.builder()
                .id(1L)
                .owner(owner)
                .maxAttendees(2)
                .build();
        when(eventMapper.toEntity(eventDto)).thenReturn(eventEntity);
        doNothing().when(eventValidator).checkIfOwnerExistsById(owner.getId());
        doThrow(new DataValidationException("Owner does not have required skills"))
                .when(eventValidator).checkIfOwnerHasSkillsRequired(eventEntity);

        assertThrows(DataValidationException.class,
                () -> eventService.create(eventDto));
    }

    @Test
    @DisplayName("Неуспешное получение созданных событий по Id юзера")
    public void testFailedGetOwnedEventsById() {
        User user1 = User.builder().id(1L).active(true).build();
        long userId = user1.getId();
        List<Event> emptyEvents = new ArrayList<>();
        Mockito.when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        Assertions.assertIterableEquals(emptyEvents, eventService.getOwnedEvents(userId));
    }

    @Test
    @DisplayName("Успешное получение всех событий по верному Id пользователя")
    public void testGetParticipatedEventsByUserId() {
        long userId = 1L;

        List<Event> mockEvents = List.of(
                Event.builder()
                        .id(21L)
                        .title("EventOne")
                        .maxAttendees(2)
                        .build(),
                Event.builder()
                        .id(22L)
                        .title("EventTwo")
                        .maxAttendees(2)
                        .build()
        );
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(mockEvents);
        List<Event> events = eventService.getParticipatedEventsByUserId(userId);

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userId);
        assertEquals(mockEvents, events);

    }

    @Test
    @DisplayName("Неуспешный поиск события по неверному Id")
    public void testFailedGetEventByIncorrectId() {
        long wrongId = 11L;
        Mockito.when(eventRepository.findById(wrongId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> eventService.getEvent(wrongId));
    }

    @Test
    @DisplayName("Успешное удаление события по верному Id")
    public void testSuccessDeleteEventById() {
        Event eventDelete = Event.builder()
                .id(5L)
                .maxAttendees(5)
                .build();
        long eventId = eventDelete.getId();

        eventService.deleteEvent(eventId);
        Mockito.verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    @DisplayName("Неуспешное удаление события по неверному Id")
    public void testFailedDeleteEventByIncorrectId() {
        long wrongId = 15L;

        Mockito.verify(eventRepository, Mockito.never()).deleteById(wrongId);
    }

    @Test
    public void successGetEvents_ByIdFilter() {
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(eventFirst, eventSecond));
        List<EventDto> resultToFilterById = eventService.getEventsByFilter(eventFilterDtoId);
        assertEquals(2, resultToFilterById.size());
    }

    @Test
    public void successGetEvents_ByTitleFilter() {
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(eventFirst, eventSecond));
        List<EventDto> resultToFilterByTitle = eventService.getEventsByFilter(eventFilterDtoTitle);
        assertEquals(1, resultToFilterByTitle.size());
    }

    @Test
    public void successGetEvents_ByStartDateFilter() {
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(eventFirst, eventSecond));
        List<EventDto> resultToFilterByStartDate = eventService.getEventsByFilter(eventFilterDtoStartDate);
        assertEquals(1, resultToFilterByStartDate.size());
    }

}