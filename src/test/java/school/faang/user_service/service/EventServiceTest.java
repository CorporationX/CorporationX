package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
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
    @InjectMocks
    private EventService eventService;

    @Test
    @DisplayName("Успешное получение всех событий по верному Id пользователя")
    public void testSuccessGetEventById() {
        Event event1 = Event.builder()
                .id(21L)
                .title("EventOne")
                .maxAttendees(2)
                .build();
        Event event2 = Event.builder()
                .id(22L)
                .title("EventTwo")
                .maxAttendees(2)
                .build();
        User user1 = User.builder().id(21L).active(true).build();
        long userId = user1.getId();
        List<User> attendees = new ArrayList<>();
        attendees.add(user1);
        event1.setAttendees(attendees);
        event2.setAttendees(attendees);
        List<Event> events = new ArrayList<>(List.of(event1, event2));

        Mockito.when(eventRepository.findAllByUserId(userId)).thenReturn(events);

        eventService.getParticipatedEventsByUserId(userId);

        Mockito.verify(eventRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("Неуспешное получение событий по не верному Id пользователя")
    public void testFailedGetEventByIncorrectId() {
        User user1 = User.builder().id(-5L).active(true).build();
        long userId = user1.getId();
        Mockito.when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        eventService.getParticipatedEventsByUserId(userId);

        Mockito.verify(eventRepository, times(1)).findAllByUserId(userId);
    }

}
