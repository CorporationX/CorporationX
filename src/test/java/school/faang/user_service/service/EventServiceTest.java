package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Spy
    private SkillMapper skillMapper = new SkillMapperImpl();
    @Mock
    private EventMapper eventMapper = new EventMapperImpl(skillMapper);
    @InjectMocks
    private EventService eventService;

    @Test
    @DisplayName("Успешное получение созданных событий по Id юзера")
    public void testSuccessGetOwnedEventsById() {
        User user1 = User.builder().id(1L).active(true).build();
        long userId = user1.getId();
        Event event1 = Event.builder()
                .id(21L)
                .title("EventOne")
                .maxAttendees(2)
                .owner(user1)
                .build();
        Event event2 = Event.builder()
                .id(22L)
                .title("EventTwo")
                .maxAttendees(2)
                .owner(user1)
                .build();
        List<Event> events = new ArrayList<>(List.of(event1, event2));
        user1.setOwnedEvents(events);

        Mockito.when(eventRepository.findAllByUserId(userId)).thenReturn(events);

        eventService.getOwnedEvents(userId);

        Mockito.verify(eventRepository, times(1)).findAllByUserId(userId);

        Assertions.assertIterableEquals(user1.getOwnedEvents(), eventRepository.findAllByUserId(userId));
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

}
