package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventOwnerIdPattern;
import school.faang.user_service.filter.event.EventStartDatePattern;
import school.faang.user_service.filter.event.EventTitlePattern;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Spy
    private SkillMapper skillMapper = new SkillMapperImpl();
    @Spy
    private EventMapper eventMapper = new EventMapperImpl(skillMapper);
    @InjectMocks
    private EventService eventService;
    @Mock
    private UserRepository userRepository;

    private List<EventFilter> eventFilters = new ArrayList<>();
    private LocalDateTime startDate;
    private User owner;
    private Skill skill;
    private Event eventFirst;
    private Event eventSecond;
    private EventFilterDto eventFilterDtoId;
    private EventFilterDto eventFilterDtoTitle;
    private EventFilterDto eventFilterDtoStartDate;

    //to testSuccessfulCreateEventIsValidate
    private EventDto eventDto;
    private Event event;
    private User user;

    //to testFailedCreateEventNotValidate
    private EventDto eventDto2;
    private Event event2;
    private User user2;
    private User user3;
    SkillDto skillDto = SkillDto.builder().id(1L).build();

    @BeforeEach
    public void init() {
        //to testCreateEventIsValidate
        user = new User();
        user.setId(1L);

        List<User> users = new ArrayList<>(List.of(user));
        Skill skill = Skill.builder()
                .id(1L)
                .build();
        user.setSkills(new ArrayList<>(List.of(skill)));

        eventDto = EventDto.builder()
                .id(1L)
                .ownerId(user.getId())
                .title("Event1")
                .relatedSkills(List.of(skillDto))
                .build();
        eventService = new EventService(eventRepository, eventMapper, userRepository);

        //to testCreateEventNotValidate
        user2 = new User();
        user2.setId(2L);

        List<User> users2 = new ArrayList<>(List.of(user));

        eventDto2 = EventDto.builder()
                .id(2L)
                .ownerId(user2.getId()).title("Event2").relatedSkills(List.of(skillDto)).build();
        event2 = eventMapper.toEntity(eventDto);

        user3 = new User();
        user3.setId(-1L);

        eventFilters.add(new EventOwnerIdPattern());
        eventFilters.add(new EventTitlePattern());
        eventFilters.add(new EventStartDatePattern());

        eventService = new EventService(eventRepository, eventMapper, eventFilters);

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
    }

    @Test
    @DisplayName("Успешное создание события")
    public void testSuccessfulCreateEventIsValid() {
        event = eventMapper.toEntity(eventDto);

        Mockito.when(eventRepository.save(event)).thenReturn(event);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        EventDto eventDto1 = eventMapper.toDto(event);
        eventService.create(eventDto1);

        ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(eventRepository, Mockito.times(1)).save(argumentCaptor.capture());
        Event capturedEvent = argumentCaptor.getValue();

        assertEquals(event.getTitle(), capturedEvent.getTitle());
        assertEquals(event.getRelatedSkills(), capturedEvent.getRelatedSkills());
    }

    @Test
    @DisplayName("Неуспешное создание события")
    public void testFailedCreateEventNotValid() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> eventService.create(eventDto2));
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
