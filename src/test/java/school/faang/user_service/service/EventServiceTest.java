package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
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
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventOwnerIdPattern;
import school.faang.user_service.filter.event.EventStartDatePattern;
import school.faang.user_service.filter.event.EventTitlePattern;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private EventMapperImpl eventMapper;
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
    public void init() {
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
