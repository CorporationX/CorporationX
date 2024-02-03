package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
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
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private UserService userService;
    @Mock
    private EventValidator eventValidator;

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

        eventService = new EventService(eventRepository, eventMapper, eventValidator, userService, eventFilters);

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
    public void successUpdateEventWhenAllConditionsValid() {
        Skill skillFirst = Skill.builder()
                .id(1L)
                .build();
        Skill skillSecond = Skill.builder()
                .id(2L)
                .build();
        List<Skill> skills = List.of(skillFirst, skillSecond);
        User owner = User.builder()
                .id(1L)
                .active(true)
                .skills(List.of(skillFirst, skillSecond))
                .build();
        List<Long> ownerSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .toList();
        EventDto eventDtoExpected = EventDto.builder()
                .id(1L)
                .ownerId(1L)
                .title("EventFirst")
                .relatedSkillIds(ownerSkillIds)
                .maxAttendees(2)
                .startDate(LocalDateTime.now().plusDays(1))
                .build();
        Event eventEntity = eventMapper.toEntity(eventDtoExpected);
        when(eventRepository.findById(eventDtoExpected.getId())).thenReturn(Optional.ofNullable(eventEntity));
        when(userService.findUserById(eventDtoExpected.getOwnerId())).thenReturn(owner);
        eventEntity.setRelatedSkills(skills);
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);

        EventDto eventDtoActual = eventService.updateEvent(eventDtoExpected);
        verify(eventRepository, times(1)).save(eventEntity);
        verify(eventMapper, times(1)).toDto(eventRepository.save(eventEntity));
        assertEquals(eventDtoExpected, eventDtoActual);
    }

    @Test
    public void shouldThrowUpdateEventWhenOwnerNotExist() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("EventFirst")
                .build();

        assertThrows(DataValidationException.class,
                () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void shouldThrowUpdateEventWhenEventNotExist() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .build();
        assertThrows(DataValidationException.class,
                () -> eventService.updateEvent(eventDto));
    }

    @Test
    public void successCreateEventWhenAllFieldsIsValid() {
        EventDto eventDto = EventDto.builder()
                .id(1L)
                .title("EventFirst")
                .ownerId(1L)
                .maxAttendees(2)
                .build();
        Event eventEntity = eventMapper.toEntity(eventDto);

        eventService.create(eventDto);
        Mockito.verify(eventRepository, times(1)).save(eventEntity);
    }

    @Test
    @DisplayName("Неуспешное получение созданных событий по Id юзера")
    public void testFailedGetOwnedEventsById() {
        User user1 = User.builder().id(1L).active(true).build();
        long userId = user1.getId();
        List<Event> emptyEvents = new ArrayList<>();
        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

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
                        .relatedSkills(List.of(Skill.builder()
                                .id(1L)
                                .build()))
                        .maxAttendees(2)
                        .build(),
                Event.builder()
                        .id(22L)
                        .title("EventTwo")
                        .relatedSkills(List.of(Skill.builder()
                                .id(2L)
                                .build()))
                        .maxAttendees(2)
                        .build()
        );
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(mockEvents);
        List<EventDto> eventsExpected = eventMapper.toListDto(mockEvents);
        List<EventDto> eventsActual = eventService.getParticipatedEventsByUserId(userId);

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userId);
        assertEquals(eventsExpected, eventsActual);
    }

    @Test
    @DisplayName("Неуспешный поиск события по неверному Id")
    public void testFailedGetEventByIncorrectId() {
        long wrongId = 11L;
        when(eventRepository.findById(wrongId)).thenReturn(Optional.empty());

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
        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    public void successGetEventDto() {
        EventDto eventDtoExpected = EventDto.builder()
                .id(1L)
                .maxAttendees(2)
                .relatedSkillIds(List.of(1L))
                .build();
        Event eventEntity = Event.builder()
                .id(1L)
                .maxAttendees(2)
                .relatedSkills(List.of(Skill.builder()
                        .id(1L)
                        .build()))
                .build();
        long eventId = eventDtoExpected.getId();
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventEntity));
        eventService.getEvent(eventDtoExpected.getId());

        EventDto eventDtoActual = eventService.getEventDto(eventId);
        assertEquals(eventDtoExpected, eventDtoActual);
    }

    @Test
    @DisplayName("Неуспешное удаление события по неверному Id")
    public void testFailedDeleteEventByIncorrectId() {
        long wrongId = 15L;

        verify(eventRepository, Mockito.never()).deleteById(wrongId);
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
